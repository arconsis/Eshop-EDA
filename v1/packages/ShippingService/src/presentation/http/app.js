const http = require('http');
const express = require('express');
const cors = require('cors');
const compress = require('compression')();
const helmet = require('helmet');
const path = require('path');
const bodyParser = require('body-parser');
const shipmentsRouter = require('./routes/shipments/router');
const errorRoute = require('./routes/errors/router');

const app = express();
app.disable('x-powered-by');
app.use(helmet());
app.use(compress);
app.use(cors());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

module.exports.init = (services) => {
  app.use(express.static(path.join(__dirname, 'public')));
  app.get('/healthCheck', ((req, res) => res.status(200).send('OK')));
  app.use('/api/v1/shipments', shipmentsRouter.init(services));
  app.use(errorRoute);
  const httpServer = http.createServer(app);
  return httpServer;
};
