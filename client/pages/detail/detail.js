// pages/detail/detail.js
const app = getApp()
const util = require('../../utils/util.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),  
    
    // mock data
    mapping: {
      '准时': 0,
      '请假': 6,
      '迟到': 2,
      '缺席': 12
    },
    point: 30,
    rehearsalRecord: [
      {
        date: "2018-06-02",
        status: '请假',
        remainPoint: 22
      },
      {
        date: "2018-05-26",
        status: '迟到',
        remainPoint: 28        
      },
      {
        date: "2018-05-19",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-12",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-05",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-28",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-21",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-19",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-12",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-05",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-28",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-21",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-19",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-12",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-05",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-28",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-21",
        status: '准时',
        remainPoint: 30
      }
    ],
    maxIdx: 6
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    util.onloadCheck(app, this)
    this.setData({
      rehearsalRecord: this.data.rehearsalRecord,
      maxIdx: maxIdx
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
  /*onShareAppMessage: function () {
  
  }*/
  loadMore: function() {
    var maxIdx = 2 * this.data.maxIdx;
    this.setData({
      maxIdx: maxIdx
    })
  },
  fold: function() {
    this.setData({
      maxIdx: 6
    })
  }
})