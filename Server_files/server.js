const http = require('http');
const express = require('express');
const bodyParser  = require('body-parser');
const performanceRouter = require('./routers/performance');
const statisticsRouter = require('./routers/statistics');
const heatmapRouter = require('./routers/heatmap');

const app = express();
const httpServer = http.createServer(app);
//require('./routes')(app);

app.use('/api', bodyParser.json());
app.use('/api', performanceRouter);
app.use('/api', statisticsRouter);
app.use('/api', heatmapRouter);

httpServer.listen(3300, function(err) {
	if(err) {
		console.log(err.message);
		return;
	}
	console.log('web server listening on port 3300');

});
