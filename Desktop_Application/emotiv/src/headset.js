/*
 * Headset
 * ********
 *
 * This example shows the different headset-related APIs, including listing
 * headsets, getting headset information, querying headsets, and checking
 * headset battery level and signal/contact quality.
 *
 */

const Cortex = require('../lib/cortex')

process.on('unhandledRejection', (err) => { throw err })

const verbose = process.env.LOG_LEVEL || 1;
class UsageError extends Error {}

const client = new Cortex({verbose});

const hs = document.getElementById("headset");
const hst = document.getElementById("headsetStr");

function getHeadSets() {
client.ready.then(() => client.init())
    .then(() => {
      return client
          .queryHeadsets()
          .then(headsets => {
            if (headsets.length) {
              //console.log('Headsets:')
              for (const headset of headsets) {
                  console.log(`   ${headset.id} [${headset.status}]`)
                  if(headset.status.toString() == "connected") {
                    hs.innerText = headset.id;
                    hst.innerText = "Connected headset";
                    document.getElementById("startBtn").disabled = false;
                  }
              }
            } else {
                hs.innerText = "";
                hst.innerText = "Please connect your headset";
                document.getElementById("startBtn").disabled = true;
                document.getElementById("recordFlag").innerText = ""
                //Promise.delay(5000).then(() => getHeadSets());
            }
              setTimeout(getHeadSets, 10000);
          })
    })
    .catch(err => {
      console.warn(`Error: ${err.message}`)
      if (err instanceof UsageError) {
        console.warn()
        console.warn(USAGE)
      }
      process.exitCode = 1
    });
}

module.exports = {
    getHeadSets,
};
