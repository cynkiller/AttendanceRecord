function loginCallback(data) {
  /*
  if (res.data.openGId) {
    obj.globalData.groupInfo.openGId = res.data.openGId;
  }
  */
  if (!data.status || data.status == "BAD") {
    // 1. bad code => weixin backend problem
    console.log("Remote backend problem. Failed to get thirdSessionKey.")
  } else if (data.status == "OK" || data.thirdSessionKey) {
    // 2. good status, get 3rd session id
    wx.setStorageSync('thirdSessionKey', data.thirdSessionKey)
    console.log(wx.getStorageSync('thirdSessionKey'))
  } else if (data.status == "NO_USER") {
    // 3. not registered => verify page => enter verify key / ask user to open in certain group chat
    wx.navigateTo({
      url: '../pages/password/password',
    })
  }
}

function postRequest(_url, sendData, func) {
  wx.request({
    url: _url,
    data: sendData,
    header: {
      'content-type': 'application/x-www-form-urlencoded',
      'Accept': 'application/json'
    },
    method: 'POST',
    success: function (res) {
      console.log(res.data)
      func(res.data);

      /*
      wx.showToast({
        title: 'Got openid',
        duration: 500
      })
      */
    },
    fail: function (res) {
      console.log(res.data)
    },
    complete: function (res) {
      //console.log(res.data)
    }
  })
}

function getRequest(_url, sendData, func) {
  wx.request({
    url: _url,
    header: {
      'content-type': 'application/json',
      'Accept': 'application/json'
    },
    method: "GET",
    success: function (res) {
      console.log(res.data)
      func(res.data)
    },
    fail: function (res) {
      console.log("request failed.")
      console.log(res)
    },
    complete: function () {
      console.log("request complete.")
    }
  })
}

const userLogin = obj => {
    // 登录
    wx.login({
      success: res => {
        console.log(res);
        obj.globalData.code = res.code;
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
              obj.globalData.userInfo = res.userInfo
              obj.globalData.encryptedData = res.encryptedData;
              obj.globalData.iv = res.iv;

              // save session data
              var sendData = {}
              sendData['code'] = obj.globalData.code;
              sendData['encryptedData'] = obj.globalData.encryptedData;
              sendData['iv'] = obj.globalData.iv
              sendData['groupData'] = obj.globalData.groupInfo.encryptedData;
              sendData['groupIv'] = obj.globalData.groupInfo.iv;
              wx.setStorageSync('sessionData', sendData)

              // 发送 res.code 到后台换取 openId, sessionKey, unionId
              postRequest('http://<yourhostname>/api/session', sendData, loginCallback)

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (obj.userInfoReadyCallback) {
                obj.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
}

module.exports = {
  userLogin: userLogin
}