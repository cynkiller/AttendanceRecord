//app.js

const req = require('utils/request.js')
const util = require('utils/util.js')

App({

  onLaunch: function (ops) {

    var that = this;
    if (ops.scene == 1044) {
      util.debug(ops.shareTicket)
      that.globalData.shareTicket = ops.shareTicket
      // 获取群信息
      // retry twice
      wx.getShareInfo({
        shareTicket: this.globalData.shareTicket,
        success: res => {
          util.debug("SUCCESS: " + res)
          that.globalData.groupInfo.encryptedData = res.encryptedData;
          that.globalData.groupInfo.iv = res.iv;
        },
        fail: res => {
          util.debug("fail: " + res)
        },
        complete: res => {
          util.debug("complete: " + res)
        }
      })
    } else {
      util.debug(ops)
    }
  
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
  
    // User login
    wx.clearStorageSync("thirdSessionKey")

  },
  globalData: {
    userInfo: null,
    code: null,
    encryptedData: null,
    iv: null,
    shareTicket: null,
    groupInfo: {
      encryptedData: null,
      iv: null,
      openGId: null
    },
    point: null
  },
  rehearsalInfo: {
    rehearsalDate: {
      date: "2018-06-06",
      startTime: "09:00",
      endTime: "12:30"
    },
    //rehearsalTime: "2018年6月2日 9:30 - 12:30",
    rehearsalLocation: "地点名称 地址",
    longitude: 121,
    latitude: 31,
    addressBook: []
  },
})
