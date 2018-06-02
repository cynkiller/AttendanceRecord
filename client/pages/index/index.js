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
      opengid: app.globalData.groupInfo.openGId
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
      title: "First App",
      path: "/pages/index/index",
      success(res) {
        console.log(res)
      }
    }
  },
  clickMe: function () {
    //this.setData({ msg: "Hello World" })
    /*
    this.setData({
      long: app.globalData.defaultlongitude,
      lati: app.globalData.defaultlatitude
    })
    */
    util.getCurrentPosition(this);
    var that = this;
    wx.chooseLocation({
      success: function (res) {
        // success
        console.log(res, "location")
        console.log(res.name)
        console.log(res.latitude)
        console.log(res.longitude)
        //app.globalData.defaultlongitude = res.longitude;
        //app.globalData.defaultlatitude = res.latitude;
        that.data.markers[0].longitude = res.longitude;
        that.data.markers[0].latitude = res.latitude;
        that.setData({
          long: res.longitude,
          lati: res.latitude,
          address: res.name + ' ' + res.address,
          markers: that.data.markers,
        })
        wx.showToast({
          title: '更新成功',
        })
      },
      fail: function () {
        // fail
      },
      complete: function () {
        // complete
      }
    })
    console.log(app.globalData.userInfo.nickName)
    console.log(app.globalData.userInfo.avatarUrl)

    /* Test https request */
    wx.request({
      url: '{yourhostname}/api/people',
      header: {
        'content-type': 'application/json',
        'Accept': 'application/json'
      },
      method: "GET",
      success: function (res) {
        //console.log(res.data._links.people.href)
        console.log(res.data)
        that.setData({
          //jsonmsg: res.data._links.people.href
          jsonmsg: res.data
        })
      },
      fail: function (res) {
        console.log("request failed.")
        console.log(res)
      },
      complete: function() {
        console.log("request complete.")
      }
    })
  }
})
