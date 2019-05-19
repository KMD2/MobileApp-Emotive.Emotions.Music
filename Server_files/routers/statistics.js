const express = require('express');
const moment = require('moment');
const statisticsRouter = express.Router();
const mysql = require('mysql');

const connection = mysql.createConnection({
	host: 'localhost',
	user: 'root',
	password: 'DBSproject2018!',
	database: 'Performance'
});


statisticsRouter.route('/statistics/:userId/:startdate/:enddate')
  .get(function(req, res) {
    var query = `SELECT CAST(sum(foc) / count(foc) as decimal(10,2)) as foc,  CAST(sum(interest) / count(interest) as decimal(10,2)) as interest, CAST(sum(rel) / count(rel) as decimal(10,2)) as rel,` +
	`  CAST(sum(str) / count(str) as decimal(10,2)) as str, CAST(sum(exc) / count(exc) as decimal(10,2)) as exc, CAST(sum(eng) / count(eng) as decimal(10,2)) as eng,` + 
	` date_format( recorded_time, '%m-%d:%H' ) as my_date FROM  measurments  WHERE user_id='${req.params.userId}' AND recorded_time BETWEEN '${req.params.startdate.replace('T', ' ')}'`+ 
                ` AND '${req.params.enddate.replace('T', ' ')}' GROUP BY my_date ORDER BY my_date`;
    connection.query(query, function(errors, rows, fields) {
            if(rows.length) {
                var result = [];
                for(var i in rows) {
                        result.push({interest:rows[i].interest, str:rows[i].str, rel:rows[i].rel, exc:rows[i].exc, eng:rows[i].eng, foc:rows[i].foc, recorded_time:rows[i].my_date});

                }
                res.contentType('application/json');            
                res.json({code: 200, data:{items: result}});
              }
            else {
                res.json({code: 400, message: 'no data'});
            }   


    });
  })

module.exports = statisticsRouter;

