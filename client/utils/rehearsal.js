function timestamps(obj) {
  var timestamps = {};
  var rehearsalDate = obj.data.rehearsalDate;
  var date = new Date();
  // Note month need to minus 1, Jun => 5
  date.setFullYear(rehearsalDate.year, rehearsalDate.month - 1, rehearsalDate.day);

  // start time
  var time = rehearsalDate.start.split(":");
  date.setHours(parseInt(time[0]), parseInt(time[1]));
  var startTimestamp = date.getTime();
  console.log("Start Date", date, "Start timestamp: ", startTimestamp)

  // end time
  time = rehearsalDate.end.split(":");
  date.setHours(parseInt(time[0]), parseInt(time[1]));
  var endTimestamp = date.getTime();
  console.log("End Date", date, "End timestamp: ", endTimestamp)

  var currentdate = new Date();
  var currentTimestamp = currentdate.getTime();
  console.log("Current Date", currentdate, "Current timestamp", currentTimestamp)

  timestamps['start'] = startTimestamp;
  timestamps['end'] = endTimestamp;
  timestamps['current'] = currentTimestamp;
  return timestamps;
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

module.exports = {
  isValidSigninTime: isValidSigninTime,
  getRemainTime: getRemainTime
}