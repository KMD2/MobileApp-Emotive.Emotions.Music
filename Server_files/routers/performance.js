const express = require('express');
const moment = require('moment');
const performanceRouter = express.Router();
const mysql = require('mysql');

const connection = mysql.createConnection({
	host: 'localhost',
	user: 'root',
	password: 'DBSproject2018!',
	database: 'Performance'
});



performanceRouter.route('/performance/:userId/:currentDate')
  .get(function(req, res) {
    var query = `SELECT *  FROM measurments WHERE user_id='${req.params.userId}' AND recorded_time>='${req.params.currentDate}'  ORDER BY recorded_time DESC`;
    connection.query(query, function(errors, rows, fields) {
	    if(rows.length) {
	        res.json({code: 200, data: {interest:rows[0].interest, str:rows[0].str, rel:rows[0].rel, exc:rows[0].exc, eng:rows[0].eng, foc:rows[0].foc, latitude:rows[0].latitude, longitude:rows[0].longitude}});
              }
	    else {
	        res.json({code: 400, message: 'no data'});
	    }	
    });
  });

performanceRouter.route('/performance/:userId')
  .post(function(req, res) {
   var currentDate = new Date();
   currentDate.setHours(currentDate.getHours()+4);
   var  cTime = moment(currentDate).format('YYYY-MM-DD HH:mm:ss');
    var query = `INSERT INTO measurments (\`user_id\`,\`interest\`, \`str\`, \`rel\`, \`exc\`, \`eng\`, \`foc\`, \`recorded_time\`, \`longitude\`, \`latitude\`) values ('${req.body.id}', ${req.body.int}, ${req.body.str}, ${req.body.rel}, ${req.body.exc}, ${req.body.eng}, ${req.body.foc},'${cTime}', ${req.body.longitude}, ${req.body.latitude})`;
    connection.query(query, function(errors, rows, fields) {
	    if(!!errors) {
		console.log(errors);
		res.json({code: 401, message: 'incorrect query'});
	    }
	    else {
		res.json({code: 200, message: 'row was added'});
	    }
    });
  });

module.exports = performanceRouter;

