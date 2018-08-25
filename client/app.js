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
  backendUser: null,
  rehearsalInfo: {
    rehearsalDate: {
      date: "1970-01-01",
      startTime: "00:00",
      endTime: "00:00",
      addrId: -1,
      isHoliday: false,
      event: null,
      state: null
    },
    address: {
      location: null,
      address: null,
      longitude: 0,
      latitude: 0
    },
    addressBook: []
  },
})
