//app.js

const req = require('utils/request.js')

App({

  onLaunch: function (ops) {

    var that = this;
    if (ops.scene == 1044) {
      console.log(ops.shareTicket)
      that.globalData.shareTicket = ops.shareTicket
      // 获取群信息
      // retry twice
      wx.getShareInfo({
        shareTicket: this.globalData.shareTicket,
        success: res => {
          console.log("SUCCESS: " + res)
          that.globalData.groupInfo.encryptedData = res.encryptedData;
          that.globalData.groupInfo.iv = res.iv;
        },
        fail: res => {
          console.log("fail: " + res)
        },
        complete: res => {
          console.log("complete: " + res)
        }
      })
    } else {
      console.log(ops)
    }
  
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    req.userLogin(this)
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
