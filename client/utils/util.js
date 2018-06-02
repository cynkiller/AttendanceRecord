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
      obj.data.markers[1].longitude = res.longitude;
      obj.data.markers[1].latitude = res.latitude;
      obj.data.markers[1].iconPath = "/resource/friendzone.png";
      obj.data.markers[1].width = 30;
      obj.data.markers[1].height = 30;
      obj.setData({
        long: res.longitude,
        lati: res.latitude,
        markers: obj.data.markers,
      })
      wx.showToast({
        title: '地理位置更新完毕'
      })
      wx.hideLoading()
    }
  })  
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

module.exports = {
  formatTime: formatTime,
  getCurrentPosition: getCurrentPosition
}