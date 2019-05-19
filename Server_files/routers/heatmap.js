const express = require('express');
const moment = require('moment');
const heatmapRouter = express.Router();
const mysql = require('mysql');

const connection = mysql.createConnection({
	host: 'localhost',
	user: 'root',
	password: 'DBSproject2018!',
	database: 'Performance'
});



heatmapRouter.route('/heatmap/:userId')
  .get(function(req, res) {
    var query = `SELECT cast(latitude as decimal(20,4)) as latitude, cast(longitude as decimal(20,4)) as longitude, cast(AVG (interest) as decimal(10,2)) as interest, cast(AVG (str) as decimal(10,2)) as str, cast(AVG (rel) as decimal(10,2)) as rel, cast(AVG (exc) as decimal(10,2)) as exc, cast(AVG (eng) as decimal(10,2)) as eng, cast(AVG (foc) as decimal(10,2)) as foc FROM measurments WHERE user_id='${req.params.userId}'  Group BY cast(latitude as decimal(20,4)),cast(longitude as decimal(20,4))`;
    console.log(query);
	connection.query(query, function(errors, rows, fields) {
	    if(rows.length) {
		var result = [];
		for(var i in rows) {
			result.push({interest:rows[i].interest, str:rows[i].str, rel:rows[i].rel, exc:rows[i].exc, eng:rows[i].eng, foc:rows[i].foc, lat:rows[i].latitude, lng:rows[i].longitude});

		}
		res.contentType('application/json');
		res.json({code: 200, data:{items: result}});
              }
	    else {
	        res.json({code: 400, message: 'no data'});
	    }
    });
  });


heatmapRouter.route('/heatmap')
  .get(function(req, res) {
    var query ='SELECT cast(latitude as decimal(20,4)) as latitude, cast(longitude as decimal(20,4)) as longitude, cast(AVG (interest) as decimal(10,2)) as interest, cast(AVG (str) as decimal(10,2)) as str, cast(AVG (rel) as decimal(10,2)) as rel, cast(AVG (exc) as decimal(10,2)) as exc, cast(AVG (eng) as decimal(10,2)) as eng, cast(AVG (foc) as decimal(10,2)) as foc FROM measurments Group BY cast(latitude as decimal(20,4)),cast(longitude as decimal(20,4))';
    console.log(query);   
    connection.query(query, function(errors, rows, fields) {
            if(rows.length) {
                var result = [];
                for(var i in rows) {
                        result.push({interest:rows[i].interest, str:rows[i].str, rel:rows[i].rel, exc:rows[i].exc, eng:rows[i].eng, foc:rows[i].foc, lat:rows[i].latitude, lng:rows[i].longitude});

                }
                res.contentType('application/json');
                res.json({code: 200, data:{items: result}});
              }
            else {
                res.json({code: 400, message: 'no data'});
            }
    });
  });

module.exports = heatmapRouter;
