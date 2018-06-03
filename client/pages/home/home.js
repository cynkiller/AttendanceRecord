// pages/home/home.js

//获取应用实例
const app = getApp()
const util = require('../../utils/util.js')
const rehearsal = require('../../utils/rehearsal.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
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
        longitude: app.rehearsalInfo.longitude,
        latitude: app.rehearsalInfo.latitude,
        width: 30,
        height: 30,
      }, // destination
      {}  // current location    
    ],
    includePoints: [
      {
        longitude: app.rehearsalInfo.longitude,
        latitude: app.rehearsalInfo.latitude,
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
    wx.getSetting({
      success: function(res) {
        if (! res.authSetting['scope.userLocation']) {
          wx.openSetting({
            success: function(res) {
              if( ! res.authSetting['scope.userLocation']) {
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
    // 地理位置权限
    wx.authorize({
      scope: "scope.userLocation",
      success: function(res) {
        console.log("success", res.errMsg)
      },
      fail: function(res) {
        console.log("fail", res.errMsg)
      },
      complete: function(res) {
        console.log("complete", res.errMsg)
      }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
  
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.onShowOperation();
    this.data.interval = setInterval(this.onShowOperation, 60000);
  },

  onShowOperation() {
    // TBD: get rehearsal info from backend
    this.data.markers[0].longitude = app.rehearsalInfo.longitude;
    this.data.markers[0].latitude = app.rehearsalInfo.latitude;
    this.data.includePoints[0].longitude = app.rehearsalInfo.longitude;
    this.data.includePoints[0].latitude = app.rehearsalInfo.latitude;
    this.setData({
      rehearsalDate: app.rehearsalInfo.rehearsalDate,
      rehearsalLocation: app.rehearsalInfo.rehearsalLocation,
      markers: this.data.markers,
      includePoints: this.data.includePoints
    })
    console.log(this.data.markers);
    util.getCurrentPosition(this);
    var remain = rehearsal.getRemainTime(this);
    if (rehearsal.isValidSigninTime(this) && this.data.signined == false) {
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
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    // 停止polling地理位置和时间
    clearInterval(this.data.interval);  
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