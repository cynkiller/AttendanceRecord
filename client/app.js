//app.js
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

    // 登录
    wx.login({
      success: res => {
        console.log(res);
        that.globalData.code = res.code;
      }
    })

    // 获取用户信息
    wx.getSetting({
      success: res => {
        console.log(res);
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            withCredentials: true,
            success: res => {
              // 可以将 res 发送给后台解码出 unionId
              // console.log(res)
              that.globalData.userInfo = res.userInfo
              that.globalData.encryptedData = res.encryptedData;
              that.globalData.iv = res.iv;

              var sendData = {}
              sendData['code'] = that.globalData.code;
              sendData['encryptedData'] = that.globalData.encryptedData;
              sendData['iv'] = that.globalData.iv
              sendData['groupData'] = that.globalData.groupInfo.encryptedData;
              sendData['groupIv'] = that.globalData.groupInfo.iv;
              // 发送 res.code 到后台换取 openId, sessionKey, unionId
              wx.request({
                url: '{yourhostname}/api/session',
                data: sendData,
                header: {
                  'content-type': 'application/x-www-form-urlencoded',
                  'Accept': 'application/json'
                },
                method: 'POST',
                success: function (res) {
                  console.log(res.data)
                  if (res.data.openGId) {
                    that.globalData.groupInfo.openGId = res.data.openGId;
                  }
                  // TBD: handle according to return data
                  // 1. bad code => weixin backend problem
                  // 2. good status, get 3rd session id
                  // 3. not registered => verify page => enter verify key / ask user to open in certain group chat
                  wx.showToast({
                    title: 'Got openid',
                    duration: 500
                  })
                },
                fail: function (res) {
                  console.log(res.data)
                },
                complete: function (res) {
                  console.log(res.data)
                }
              })

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
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
      year: 2018,
      month: 6,
      day: 4,
      start: "0:30",
      end: "1:30"
    },
    //rehearsalTime: "2018年6月2日 9:30 - 12:30",
    rehearsalLocation: "地点名称 地址",
    longitude: 121,
    latitude: 31,
    addressBook: []
  },
})
