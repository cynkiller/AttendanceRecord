const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}

const toReadableDate = date => {
  var ymd = date.split("-");
  var date = ymd[0] + '年' + parseInt(ymd[1]) + '月' + parseInt(ymd[2]) + '日';
  return date;
}

const getCurrentPosition = obj => {
  wx.showLoading({
    title: '获取当前地理位置...'
  })
  wx.getLocation({
    type: 'gcj02', //'wgs84' return gps position,
    success: function (res) {
      console.log("Current location")
      console.log("latitude", res.latitude)
      console.log("longitude", res.longitude)
      console.log(obj.data.markers, obj.data.includePoints)
      obj.data.markers[1].longitude = res.longitude;
      obj.data.markers[1].latitude = res.latitude;
      obj.data.markers[1].iconPath = "/resource/meilin.jpg";//"/resource/friendzone.png";
      obj.data.markers[1].width = 30;
      obj.data.markers[1].height = 30;
      obj.data.markers[1].title = "当前地点";
      obj.data.includePoints[1].longitude = res.longitude;
      obj.data.includePoints[1].latitude = res.latitude;
      obj.setData({
        long: res.longitude,
        lati: res.latitude,
        markers: obj.data.markers,
        includePoints: obj.data.includePoints
      })
      var includePoints = obj.data.includePoints;
      var dist = getGpsDisance(includePoints[0].latitude, includePoints[0].longitude, includePoints[1].latitude, includePoints[1].longitude)
      obj.setData({
        distance: (dist).toFixed(0)
      })
      /*
      wx.showToast({
        title: '地理位置更新完毕',
        duration: 500
      })
      */
      wx.hideLoading()
    }
  })  
}

function toRad(d) { return d * Math.PI / 180; }

function getGpsDisance(lat1, lng1, lat2, lng2) {
  console.log(lat1, lng1, lat2, lng2)
  var dis = 0;
  var radLat1 = toRad(lat1);
  var radLat2 = toRad(lat2);
  var deltaLat = radLat1 - radLat2;
  var deltaLng = toRad(lng1) - toRad(lng2);
  var dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaLng / 2), 2)));
  return dis * 6378137;
}

/* ref: https://blog.csdn.net/xiejm2333/article/details/73297004
public static double algorithm(double longitude1, double latitude1, double longitude2, double latitude2) {
      double Lat1 = rad(latitude1); // 纬度
      double Lat2 = rad(latitude2);
      double a = Lat1 - Lat2;//两点纬度之差
      double b = rad(longitude1) - rad(longitude2); //经度之差
      double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));//计算两点距离的公式
      s = s * 6378137.0;//弧长乘地球半径（半径为米）
      s = Math.round(s * 10000d) / 10000d;//精确距离的数值
      return s;
}

private static double rad(double d) {

      return d * Math.PI / 180.00; //角度转换成弧度

}
*/

const sleep = d => {
  for (var t = Date.now(); Date.now() - t <= d;);
}

const onloadCheck = (app, obj) => {
  if (app.globalData.userInfo) {
    obj.setData({
      userInfo: app.globalData.userInfo,
      hasUserInfo: true
    })
  } else if (obj.data.canIUse) {
    // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
    // 所以此处加入 callback 以防止这种情况
    app.userInfoReadyCallback = res => {
      obj.setData({
        userInfo: res.userInfo,
        hasUserInfo: true
      })
    }
  } else {
    // 在没有 open-type=getUserInfo 版本的兼容处理
    wx.getUserInfo({
      success: res => {
        app.globalData.userInfo = res.userInfo
        obj.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    })
  }
}

const TempMessage = (obj, varname) => {
  console.log(varname)
  var data = {};
  data[varname] = true;
  obj.setData(data)
  setTimeout(function () {
    data[varname] = false;
    obj.setData(data);
  }, 1000);    
}

const navigate =  event => {
  var target = event.currentTarget.dataset.target;
  wx.navigateTo({
    url: '/pages/' + target + '/' + target,
  })
}

const debug = (...msg) => {
  // To be removed in production
  console.log(msg)
}

const info = (...msg) => {
  console.log(msg)
}

module.exports = {
  formatTime: formatTime,
  getCurrentPosition: getCurrentPosition,
  getGpsDisance: getGpsDisance,
  toReadableDate: toReadableDate,
  sleep: sleep,
  onloadCheck: onloadCheck,
  TempMessage: TempMessage,
  navigate: navigate,
  debug: debug,
  info: info
}