// pages/home/home.js

//获取应用实例
const app = getApp()
const util = require('../../utils/util.js')
const req = require('../../utils/request.js')
const rehearsal = require('../../utils/rehearsal.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showpage: false,
    disableSignin: false,
    signined: false,
    distance: null,
    rehearsalStarted: false,
    remainMinutes: null,
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    markers: [
      {
        id: 0,
        iconPath: "/resource/position.png",
        longitude: null,
        latitude: null,
        width: 30,
        height: 30,
      }, // destination
      {}  // current location    
    ],
    includePoints: [
      {
        longitude: null,
        latitude: null,
      },
      {}
    ],
    interval: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    wx.showShareMenu({
      withShareTicket: true //要求小程序返回分享目标信息
    })

    req.weixinUserLogin(app, true, this.showpage)


    // 地理位置权限
    wx.authorize({
      scope: "scope.userLocation",
      success: function (res) {
        util.debug("userLocation success", res.errMsg)
      },
      fail: function (res) {
        util.debug("userLocation fail", res.errMsg)
      },
      complete: function (res) {
        util.debug("userLocation complete", res.errMsg)
      }
    })

    wx.getSetting({
      success: function (res) {
        if (!res.authSetting['scope.userLocation']) {
          wx.showToast({
            title: '小程序需要地理位置权限'
          })
          wx.openSetting({
            success: function (res) {
              if (!res.authSetting['scope.userLocation']) {
                wx.showToast({
                  title: '获取地理位置失败',
                })
                // 设置flag不要获取地理位置
              }
            }
          })
        }
      }
    })

  },

  showpage: function() {
    util.info("showpage")
    this.setData({
      showpage: true
    })
    this.onShow()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
  
  },

  onPullDownRefresh: function() {
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    if(this.data.showpage) {
      this.onShowOperation();
      if(this.data.interval !== undefined) {
        this.data.interval = setInterval(this.onShowOperation, 60000);
      }
    }
  },

  onShowOperation() {
    // TBD: get rehearsal info from backend
    this.data.markers[0].longitude = app.rehearsalInfo.longitude;
    this.data.markers[0].latitude = app.rehearsalInfo.latitude;
    this.data.includePoints[0].longitude = app.rehearsalInfo.longitude;
    this.data.includePoints[0].latitude = app.rehearsalInfo.latitude;
    this.setData({
      rehearsalDate: app.rehearsalInfo.rehearsalDate,
      readableDate: util.toReadableDate(app.rehearsalInfo.rehearsalDate.date),
      rehearsalLocation: app.rehearsalInfo.rehearsalLocation,
      markers: this.data.markers,
      includePoints: this.data.includePoints
    })
    util.debug(this.data.markers);
    util.getCurrentPosition(this);
    var distance = util.getGpsDisance(
          this.data.includePoints[0].latitude,
          this.data.includePoints[0].longitude,
          this.data.includePoints[1].latitude,
          this.data.includePoints[1].longitude);
    util.debug("distance:", distance)
    var remain = rehearsal.getRemainTime(this);
    if (rehearsal.isValidSigninTime(this) && this.data.signined == false && distance <= 10) {
      remain = (remain / 1000 / 60 / 60).toFixed(2);
      this.setData({
        disableSignin: false,
        remainHours: remain
      })
    } else {
      if (remain != null)
        remain = (remain / 1000 / 60 / 60).toFixed(2);
      this.setData({
        disableSignin: true,
        remainHours: remain
      })
      if (!rehearsal.isValidSigninTime(this)) {
        wx.showToast({
          title: '不在打卡时间内',
          duration: 2000
        })
      } else if (distance > 10) {
        wx.showToast({
          title: '不在打卡距离内',
          duration: 2000
        })
      }
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    // 停止polling地理位置和时间
    if(this.data.showpage) {
        util.debug("clearIntervfal", this.data.interval)
        clearInterval(this.data.interval);  
    }
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
  
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
  
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
  
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
    return {
      title: '打卡神器',
      path: "/pages/home/home"
    }
  },

  /*********************
   * Customized functions
   *********************/
  signin: function() {
    // TBD
    // 检查是否迟到
    rehearsal.isValidSigninTime(this)
    // 更新后台信息
    wx.showToast({
      title: '打卡成功',
    })
    this.setData({
      disableSignin: true,
      signined: true
    })
  }
})