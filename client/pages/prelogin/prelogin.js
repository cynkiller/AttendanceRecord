// pages/prelogin/prelogin.js
const req = require('../../utils/request.js')
const util = require('../../utils/util.js')
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    canIUse: false,
    info: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var msg;
    switch(options['info']) {
      case "user":
        msg = "数据解析错误";
        this.setData({
          canIUse: false
        })
        break;
      case "backend":
        msg = "Oops, 连不上服务器";
        this.setData({
          canIUse: true
        })
        break;
    }
    this.setData({
      info: msg
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

  getUserInfo: function (e) {
    util.debug(e)
    app.globalData.userInfo = e.detail.userInfo
    // User login
    wx.showLoading({
      title: '登陆中',
    })
    req.weixinUserLogin(app, true, this.goodlogin)
  },

  goodlogin: function () {
    wx.hideLoading()
    wx.reLaunch({
      url: '/pages/home/home',
    })
  }
})