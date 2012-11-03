/**
 * Provide client side logging capabilities to AJAX applications.
 *
 * @author <a href="mailto:thespiegs@users.sourceforge.net">Eric Spiegelberg</a>
 * @see <a href="http://sourceforge.net/projects/log4ajax">Log4Ajax</a>
 */

function LOG() {
  throw "Do not instantiate Log";
}

LOG.Level = function(name, priority, color) {
  this.name = name;
  this.priority = priority;
  this.color = color;
}

LOG.OFF = new LOG.Level("off", 1000);
LOG.FATAL = new LOG.Level("fatal", 900, "red");
LOG.ERROR = new LOG.Level("error", 800, "red");
LOG.WARN = new LOG.Level("warn", 500, "yellow");
LOG.INFO = new LOG.Level("info", 400, "black");
LOG.DEBUG = new LOG.Level("debug", 300, "green");
LOG.ALL = new LOG.Level("all", 100);

LOG.LEVEL = LOG.ALL;

LOG.consoleDivId = "logConsole";

LOG.debug = function(msg, pre) {
  LOG._log(msg, LOG.DEBUG, pre);
}

LOG.info = function(msg, pre) {
  LOG._log(msg, LOG.INFO, pre);
}

LOG.warn = function(msg, pre) {
  LOG._log(msg, LOG.WARN ,pre);
}

LOG.error = function(msg, pre) {
  LOG._log(msg, LOG.ERROR, pre);
}

LOG.fatal = function(msg, pre) {
  LOG._log(msg, LOG.FATAL, pre);
}

LOG._log = function(msg, level, pre) {
  if (level.priority >= LOG.LEVEL.priority) {
    LOG._logToConsole(msg, level, pre);
    // TODO: transmit log message to server via AJAX
  }
}

LOG._logToConsole = function(msg, level, preformat) {
  var consoleDiv = document.getElementById(LOG.consoleDivId);
  if (consoleDiv) {
    var div = document.createElement("div");
    div.appendChild(document.createTextNode(new Date().toLocaleString() + ' '));
    var span = document.createElement("span");
    span.style.color = level.color;
    span.appendChild(document.createTextNode(level.name + ": "));
    div.appendChild(span);
    div.appendChild(document.createTextNode(msg));
    if (preformat) {
      var pre = document.createElement("pre");
      pre.appendChild(document.createTextNode(preformat));
      div.appendChild(pre);
    }
    consoleDiv.appendChild(div);
    div.scrollIntoView(false);
  }
}

LOG.clear = function() {
	var consoleDiv = document.getElementById(LOG.consoleDivId);
	consoleDiv.innerHTML = '';
}
