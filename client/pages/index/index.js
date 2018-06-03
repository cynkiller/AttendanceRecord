//index.js

//获取应用实例
const app = getApp()

// 申请mapsdk还要绑定qq号一刚！
//var QQMapWX = require('../../libs/qqmap-wx-jssdk.js');
//var qqmapsdk;
const util = require('../../utils/util.js')

Page({
  data: {
    motto: 'Hello World',
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    administrator: true,
    markers: [
      {
        id: 0,
        iconPath: "/resource/position.png",
        longitude: app.globalData.defaultlongitude,
        latitude: app.globalData.defaultlatitude,
        width: 30,
        height: 30,
      }, // destination
      {}  // current location    
    ],
    includePoints: [
      {
        longitude: app.globalData.defaultlongitude,
        latitude: app.globalData.defaultlatitude,
      },
      {}
    ]
  },
  //事件处理函数
  bindViewTap: function() {
    wx.switchTab({ //navigateTo not useful when destination is a tab
      url: '../logs/logs'
    })
  },
  onLoad: function () {
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
    } else if (this.data.canIUse){
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true
          })
        }
      })
    }
    util.getCurrentPosition(this)
    wx.showShareMenu({
      withShareTicket: true //要求小程序返回分享目标信息
    })
    this.setData({
      opengid: app.globalData.groupInfo.openGId,
      point: 30
    })
  },
  getUserInfo: function(e) {
    console.log(e)
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
  },
  onShareAppMessage: function () {
    return {
      title: "打卡神器",
      path: "/pages/index/index",
      success(res) {
        console.log(res)
      }
    }
  },
  navigate: function(event) {
    var target = event.currentTarget.dataset.target;
    wx.navigateTo({
      url: '../' + target + '/' + target,
    })
  }
})