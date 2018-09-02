//index.js

//获取应用实例
const app = getApp()
const request = require('../../utils/request.js')

// 申请mapsdk还要绑定qq号一刚！
//var QQMapWX = require('../../libs/qqmap-wx-jssdk.js');
//var qqmapsdk;
const util = require('../../utils/util.js')

Page({
  data: {
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  //事件处理函数
  bindViewTap: function() {
    wx.switchTab({ //navigateTo not useful when destination is a tab
      url: '../logs/logs'
    })
  },
  onLoad: function () {
    util.onloadCheck(app, this)
    //util.getCurrentPosition(this)
    wx.showShareMenu({
      withShareTicket: true //要求小程序返回分享目标信息
    })
    this.setData({
      opengid: app.globalData.groupInfo.openGId
    })
  },
  onShow: function() {
    if (app.backendUser) {
      this.setUserInfo(this, app.backendUser)
    } else {
      request.getRequest("/queryUserInfo", request.getUserInfo, this.setUserInfo, this);
    }
  },
  setUserInfo: function(obj, data) {
    util.info("enter setUserInfo")
    if (data.hasOwnProperty("data")) data = data.data;
    obj.setData({
      point: data.point,
      nickName: data.nickName,
      authority: data.authority
    })
  },
  getUserInfo: function(e) {
    util.debug(e)
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
        util.debug(res)
      }
    }
  },
  navigate: function(event) {
    util.navigate(event)
  },

  askleaveCallback: function(data, callback, obj) {
    util.info("enter askleaveCallback")
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
    } else if (data.status == "CLIENT_ASKLEAVE_TOOLATE") {
      wx.showToast({
        title: '不能请假了',
      })
    } else if (data.status == "SERVER_INTERNAL_ERROR") {
      obj.setData({
        updatefail: true,
        failmsg: "程序出了个bug！0.0"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "GENERAL_OK") {
      util.info("Update successful.")
      wx.showToast({
        title: '请假成功',
      })
    }
  },

  askleave: function() {
    var that = this
    wx.showModal({
      title: '确认请假？',
      content: "下次排练时间是: " + util.toReadableDate(app.rehearsalInfo.rehearsalDate.date),
      confirmText: "确认",
      cancelText: "再想想",
      success: function (res) {
        util.debug(res);
        if (res.confirm) {
          util.debug('用户点击主操作')
          // TBD: update database
          // TBD: check result
          request.postRequest('/askLeave', {}, that.askleaveCallback, null, that);
          wx.showToast({
            title: '请假成功',
          })
        } else {
          util.debug('用户点击辅助操作')
        }
      }
    });
  },
  onPullDownRefresh : function() {
    request.getRequest("/queryUserInfo", request.getUserInfo, this.setUserInfo, this);
  }
})