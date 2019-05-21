const Cortex = require("../lib/cortex");
const geolocation_ = require('geolocation');
const async = require('async');
const http = require('http');


function rollingAverage(columns, windowSize) {
  let avgCount = 0;
  const averages = {};
  return row => {
    avgCount = Math.min(windowSize, avgCount + 1);

    columns.forEach((col, i) => {
      const oldAvg = averages[col] || 0;
      averages[col] = oldAvg + (row[i] - oldAvg) / avgCount;
    });

    return averages;
  };
}

function numbers(client, windowSize, onResult) {
  return client
    .createSession({ status: "open" })
    .then(() => client.subscribe({ streams: ["met"] }))
    .then(_subs => {
      const subs = Object.assign({}, ..._subs);
      if (!subs.met)
        throw new Error("failed to subscribe");


      // level it's averaged over 10s anyway
      const averageMet = rollingAverage(subs.met.cols, 1);

      const data = {};
      for (const col of [...subs.met.cols]) {
        data[col] = 0;
      }

      const onMet = ev => maybeUpdate("met", averageMet(ev.met));
      client.on("met", onMet);

      // stream to send everything - that stream must be the fastest.
      let hasUpdate = {};
      const maybeUpdate = (key, newdata) => {
        if (hasUpdate[key]) {
          onResult(data);
          hasUpdate = {};
        }
        hasUpdate[key] = true;
        Object.assign(data, newdata);
      };

      return () =>
        client
          .unsubscribe({ streams: ["met"] })
          .then(() => client.updateSession({ status: "close" }));
    });
}

module.exports = {
    getData: function()
    {
        process.on("unhandledRejection", err => {
            throw err;
        });

        // We can set LOG_LEVEL=2 or 3 for more detailed errors
        const verbose = process.env.LOG_LEVEL || 1;
        const options = {verbose};
        const avgWindow = 10;

        const client = new Cortex(options);

        client.ready.then(() => client.init()).then(() =>
            numbers(client, avgWindow, averages => {
                const output = Object.keys(averages)
                    .map(k => `${k}:${averages[k].toFixed(2)}`);

                let post_array = {}

                for (var o in output) {
                    post_array[output[o].split(':')[0]] = parseFloat(output[o].split(':')[1])
                }

                navigator.geolocation.getCurrentPosition(function (position) {

                    post_array['latitude'] = position.coords.latitude;
                    post_array['longitude'] = position.coords.longitude;
                    post_array['id'] = document.getElementById("headset").innerText;

                    var post_data = JSON.stringify(post_array);
                    console.log(post_data);

                    var post_options = {
                        hostname: 'ec2-52-14-214-97.us-east-2.compute.amazonaws.com',
                        port: 3300,
                        path : '/api/performance/' + post_array['id'],
                        method : 'POST',
                        headers : {
                            'Content-Type': 'application/json',
                            'Content-Length': Buffer.byteLength(post_data)
                            //'Authorization': 'Bearer xxxxxxxxxxxxxxxxxxxxxxxxxx'
                        }
                    };

                    post_req = http.request(post_options, function (res) {
                        console.log('STATUS:' + res.statusCode)
                    });

                    post_req.write(post_data);
                    post_req.end();

                    }, function (error) {
                        console.log(error)
                    }, {enableHighAccuracy: true});
            })
        );
    }
}

