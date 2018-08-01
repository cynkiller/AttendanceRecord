// pages/userinfo/userinfo.js

const app = getApp()
const util = require('../../utils/util.js')
const request = require('../../utils/request.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    voicepart: ["女高一", "女高二", "女中一", "女中二", "男高一", "男高二", "男低一", "男低二"],
    voicepartIndex: 0,

    status: ["成员", "暂离"],
    statusIndex: 0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    util.onloadCheck(app, this)
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    // Current value
    var sendData = {}
    request.getRequest("/queryUserInfo", this.setDefaultInfo, this);
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
      // TBD: Get current data from backend
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
  
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
  
  },

  /**
   * Self-defined functions
   */
  bindVoicepartChange: function(e) {
    this.setData({
      voicepartIndex: e.detail.value
    })
  },

  bindStatusChange: function(e) {
    this.setData({
      statusIndex: e.detail.value
    })
  },

  formSubmit: function(event) {
    util.debug(event.detail.value)
    var sendData = {}
    sendData['nickname'] = event.detail.value.nickname; // backend check 
    sendData['realname'] = event.detail.value.realname; // backend check
    sendData['voicepart'] = this.data.voicepartIndex;
    sendData['status'] = this.data.statusIndex;
    request.postRequest("/setUserinfo", sendData, this.formSubmitCallback, null, this);
  },

  formSubmitCallback: function(data, func, obj) {
    util.info("enter userinfo formSubmitCallback")
    if (!data.status) {
      util.info("Remote backend problem. Failed to change userinfo.")
      obj.setData( {
        updatefail: true,
        failmsg: "无法连接服务器。。更新失败"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        }, obj)
      }, 3000)
    } else if (data.status == "SERVER_SESSION_EXPIRED") {
      util.info("Login session expired.")
      app.loginReady = false;
      obj.setData({
        updatefail: true,
        failmsg: "重新登陆中。。"
      })
      // relogin
      request.weixinUserLogin(app, true, function(obj){
          obj.setData({updatefail: false})
        }, obj)
    } else if (data.status == "GENERAL_OK") {
      util.info("Update successful.")
      wx.showToast({
        title: '更新成功',
      })
      setTimeout( function() {
        wx.switchTab({
          url: '/pages/index/index',
        })
      }, 3000);
    }
  },

  setDefaultInfo: function(data, obj) {
    util.info("enter userinfo setDefaultInfo")
    if (!data.status) {
      util.info("Remote backend problem. Failed to change userinfo.")
      obj.setData({
        updatefail: true,
        failmsg: "无法连接服务器。。更新失败"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        }, obj)
      }, 3000)
    } else if (data.status == "SERVER_SESSION_EXPIRED") {
      util.info("Login session expired.")
      console.log(obj)
      app.loginReady = false;
      obj.setData({
        updatefail: true,
        failmsg: "重新登陆中。。"
      })
      // relogin
      request.weixinUserLogin(app, true, function (obj) {
        obj.setData({ updatefail: false })
      }, obj)
    } else if (data.status == "GENERAL_OK") {
      util.info("Get userinfo success.")
      obj.setData({
        nickName: data.data.nickName,
        realName: data.data.realName,
        voicepartIndex: data.data.voicePart,
        stateIndex: data.data.state
      })
      util.debug(data)
    }    
  }
})