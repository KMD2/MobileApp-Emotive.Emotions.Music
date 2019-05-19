const express = require('express');
const moment = require('moment');
const performanceRouter = express.Router();
const mysql = require('mysql');
const dateFormat = require('dateformat');
const connection = mysql.createConnection({
	host: 'localhost',
	user: 'root',
	password: 'DBSproject2018!',
	database: 'Performance'
});

const userId = "EPOCPLUS-3b9ae818";
//const userId = "INSIGHT-5a688f1b";

var currentDate = new Date();
currentDate.setDate(currentDate.getDate() - 10);

     for (var i = 0; i < 10; i++) {

	//var lng = Math.random() * (55.5 - 54.5) + 54.5;
///	var lat = Math.random() * (24.8 - 24.5) + 24.5;

        var lng = Math.random() * (55.5 - 54.5) + 54.5;
        var lat = Math.random() * (24.3 - 24) + 24; 
 
	var interest  =0.8 //Math.random(0.0, 1.0);
	var focus = 0.13 //Math.random(0.0, 1.0);
	var stress = 0.3 //Math.random(0.0, 1.0);
	var exct = 0.9 //Math.random(0.0, 1.0);
	var eng = 0.4 //Math.random(0.0, 1.0);
	var rel = 0.7 //Math.random(0.0, 1.0);
	var cTime = new Date("yyyy-MM-dd hh:mm:ss");
	currentDate.setSeconds(currentDate.getSeconds() + 10);
	queryDate = currentDate.toISOString().slice(0,19).replace('T', ' ');
//	queryDate = dateFormat(currentDate, "yyyy-MM-dd HH:mm:ss");


	var query = `INSERT INTO measurments (\`user_id\`,\`interest\`, \`str\`, \`rel\`, \`exc\`, \`eng\`, \`foc\`,` +
			` \`recorded_time\`, \`longitude\`, \`latitude\`) values ('${userId}', ${interest}, ${stress},` +
			` ${rel}, ${exct}, ${eng}, ${focus},'${queryDate}', ${lng}, ${lat})`;
   	
	//currentDate.toISOString();
	
	connection.query(query, function(errors, rows, fields) { });
   

  }      


console.log("done");

//}
