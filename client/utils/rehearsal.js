const util = require("util.js");
const request = require("request.js")
const app = getApp();

function toTimestamp(_date, _time) {
  var date = new Date();
  // Note month need to minus 1, Jun => 5
  var ymd = _date.split('-');
  date.setFullYear(ymd[0], ymd[1] - 1, ymd[2]);

  // time
  var time = _time.split(":");
  date.setHours(parseInt(time[0]), parseInt(time[1]));

  util.debug(date)
  return date.getTime();
}

function timestamps(obj) {
  var timestamps = {};
  var rehearsalDate = obj.data.rehearsalDate;
  var startTimestamp = toTimestamp(rehearsalDate.date, rehearsalDate.startTime)
  var endTimestamp = toTimestamp(rehearsalDate.date, rehearsalDate.endTime)
  util.debug("Start timestamp: ", startTimestamp)
  util.debug("End timestamp: ", endTimestamp)

  var currentdate = new Date();
  var currentTimestamp = currentdate.getTime();
  util.debug("Current Date", currentdate, "Current timestamp", currentTimestamp)

  timestamps['start'] = startTimestamp;
  timestamps['end'] = endTimestamp;
  timestamps['current'] = currentTimestamp;
  return timestamps;
}

function timestampToTime(ts) {
  var date = new Date(ts);
  var hours = date.getHours();
  var minutes = "0" + date.getMinutes();
  //var seconds = "0" + date.getSeconds();
  //var formattedTime = hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
  var formattedTime = hours + ':' + minutes.substr(-2)
  return formattedTime;
}

const isValidSigninTime = obj => {
  var ts = timestamps(obj);
  if (ts['current'] >= ts['start'] && ts['current'] <= ts['end'])
    return true;
  else
    return false;
}

const getRemainTime = obj => {
  var ts = timestamps(obj);
  if ( ts['current'] < ts['start'])
    return ts['start'] - ts['current'];
  else if ( ts['current'] < ts['end']) {
    obj.setData({
      rehearsalStarted: true
    })
    return ts['current'] - ts['start'];
  }
  else
    return null;
}

function getNextRehearsalCallback(data, callback, obj) {
  //util.debug(data)
  util.info("enter getNextRehearsalCallback")
  if (!data.status) {
    util.info("Remote backend problem. Failed to change userinfo.")
    obj.setData({
      updatefail: true,
      failmsg: "无法连接服务器。。更新失败"
    })
    setTimeout(function (obj) {
      obj.setData({
        updatefail: false
      })
    }, 3000, obj)
  } else if (data.status == "SERVER_SESSION_EXPIRED") {
    util.info("Login session expired.")
    app.loginReady = false;
    obj.setData({
      updatefail: true,
      failmsg: "重新登陆中。。"
    })
    // relogin
    request.weixinUserLogin(app, true, function (obj) {
      obj.setData({ updatefail: false })
    }, obj)
  } else if (data.status == "SERVER_INTERNAL_ERROR") {
    util.info("Server internal error.")
    // TBD
  } else if (data.status == "GENERAL_OK") {
    util.info("Update successful.")
    app.rehearsalInfo.rehearsalDate = JSON.parse(JSON.stringify(data.data));
    app.rehearsalInfo.rehearsalDate.startTime = timestampToTime(app.rehearsalInfo.rehearsalDate.startTimestamp);
    app.rehearsalInfo.rehearsalDate.endTime = timestampToTime(app.rehearsalInfo.rehearsalDate.endTimestamp);
    app.rehearsalInfo.address = JSON.parse(JSON.stringify(data.address));
    callback(obj)
  }
}

function getNextRehearsal(obj, callback) {
  request.getRequest("/getRehearsalInfo", getNextRehearsalCallback, callback, obj);
}

module.exports = {
  isValidSigninTime: isValidSigninTime,
  toTimestamp: toTimestamp,
  timestampToTime: timestampToTime,
  getRemainTime: getRemainTime,
  getNextRehearsal: getNextRehearsal
}