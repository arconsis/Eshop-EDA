const http = require('http');
const express = require('express');
const cors = require('cors');
const compress = require('compression')();
const helmet = require('helmet');
const path = require('path');
const ordersRouter = require('./routes/orders/router');
const errorRoute = require('./routes/errors/router');

const app = express();
// The request handler must be the first middleware on the app
app.disable('x-powered-by');
app.use(helmet());
app.use(compress);
app.use(cors());

module.exports.init = (services) => {
  app.use(express.static(path.join(__dirname, 'public')));
  app.get('/healthCheck', ((req, res) => res.status(200).send('OK')));
  app.use(ordersRouter.init(services));
  app.use(errorRoute);
  const httpServer = http.createServer(app);
  return httpServer;
};
