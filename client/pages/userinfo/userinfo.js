// pages/userinfo/userinfo.js

const app = getApp()
const util = require('../../utils/util.js')

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
  }
})