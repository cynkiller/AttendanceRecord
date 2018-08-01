var baseUrl = "";

const util = require('util.js');

function loginCallback(data, func, parm) {
  util.info("enter loginCallback")
  const app = getApp();
  /*
  if (res.data.openGId) {
    obj.globalData.groupInfo.openGId = res.data.openGId;
  }
  */
  if (!data.status) {
    util.info("Remote backend problem. Failed to get thirdSessionKey.")
    wx.reLaunch({
      url: '/pages/prelogin/prelogin?info=backend',
    })
  } else if(data.status == "CLIENT_BAD_DATA") {
    // 1. bad code => weixin backend problem
    util.info("Failed to get user information.")
    app.loginReady = false;
    wx.reLaunch({
      url: '/pages/prelogin/prelogin?info=user',
    })
  } else if (data.status == "GENERAL_OK" || data.thirdSessionKey) {
    // 2. good status, get 3rd session id
    wx.setStorageSync('thirdSessionKey', data.thirdSessionKey)
    util.debug(wx.getStorageSync('thirdSessionKey'))
    app.loginReady = true;
    if(func) func(parm)
  } else if (data.status == "SERVER_NO_USER") {
    // 3. not registered => verify page => enter verify key / ask user to open in certain group chat
    app.loginReady = false;
    wx.reLaunch({
      url: '/pages/password/password',
    })
  }
}

function getUserInfo(data, obj, func) {
  util.info("enter userinfo setDefaultInfo")
  const app = getApp();
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
    weixinUserLogin(app, true, function (obj) {
      obj.setData({ updatefail: false })
    }, obj)
  } else if (data.status == "GENERAL_OK") {
    util.info("Get userinfo success.")
    util.debug(data)
    app.backendUser = {}
    app.backendUser.point = data.data.point;
    app.backendUser.authority = data.data.authority;
    app.backendUser.nickName = data.data.nickName;
    app.backendUser.realName = data.data.realName;
    app.backendUser.state = data.data.state;
    app.backendUser.voicePart = data.data.voicePart;
    if (func) {
      util.debug("enter function")
      func(obj, data)
    }
  }
}

function postRequest(_urlalias, sendData, func, callback, parm) {
  util.debug("enter postRequest", _urlalias)
  wx.request({
    url: baseUrl + _urlalias,
    data: sendData,
    header: {
      'content-type': 'application/x-www-form-urlencoded',
      'Accept': 'application/json',
      'thirdSessionKey': wx.getStorageSync("thirdSessionKey")
    },
    method: 'POST',
    success: function (res) {
      util.debug(res.data)
      if (func)
        func(res.data, callback, parm);

      /*
      wx.showToast({
        title: 'Got openid',
        duration: 500
      })
      */
    },
    fail: function (res) {
      util.debug(res.data)
      wx.navigateTo({
        url: '/pages/prelogin/prelogin?info=backend',
      })
    },
    complete: function (res) {
      //console.log(res.data)
    }
  })
}

function getRequest(_urlalias, func, parm = null, callback = null) {
  wx.request({
    url: baseUrl + _urlalias,
    header: {
      'content-type': 'application/json',
      'Accept': 'application/json',
      'thirdSessionKey': wx.getStorageSync("thirdSessionKey")
    },
    method: "GET",
    success: function (res) {
      util.debug(res.data)
      func(res.data, parm, callback)
    },
    fail: function (res) {
      util.info("getRequest request failed.")
      util.debug(res)
    },
    complete: function () {
      util.info("getRequest request complete.")
    }
  })
}

const backendLogin = (func, parm) => {
    // 发送 res.code 到后台换取 openId, sessionKey, unionId
    var sendData = wx.getStorageSync('sessionData')
    util.debug("backendLogin: ", sendData)
    postRequest("/session", sendData, loginCallback, func, parm)
}

function getUserSetting(obj, backend, func, parm) {
  util.info("enter getUserSetting")
  // 获取用户信息
  wx.getSetting({
    success: res => {
      util.debug(res);
      util.debug("getSetting success:", res)
      if (res.authSetting['scope.userInfo']) {
        // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
        wx.getUserInfo({
          withCredentials: true,
          success: res => {
            util.debug("getUserInfo success:", res)
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

            if (backend) {
              // Connect server for session establish
              backendLogin(func, parm)
            } else {
              // No backend connection
              func(parm)
            }
            // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
            // 所以此处加入 callback 以防止这种情况
            if (obj.userInfoReadyCallback) {
              obj.userInfoReadyCallback(res)
            }
          },
          fail: res => {
            util.debug("getUserInfo fail:", res)
          }
        })
      } else {
        util.info("weixinUserLogin failed to get user information.")
        wx.reLaunch({
          url: '/pages/prelogin/prelogin?info=user',
        })
      }
    },
    fail: res => {
      util.debug("getSetting fail:", res)
      util.info("Weixin server problem. Failed to login.")
      wx.reLaunch({
        url: '/pages/prelogin/prelogin?info=weixin',
      })
    },
    complete: res => {
      util.debug("getSetting complete:", res)
    }
  })
}

const weixinUserLogin = (obj, backend = true, func, parm) => {
    util.debug("enter weixinUserLogin, backend = ", backend)

    // 检查是否已经登录
    if (backend && obj.loginReady) {
      util.debug("login direct return")
      func(parm);
      return
    }
  
    // 登录
    wx.login({
      success: res => {
        util.debug("login success: ", res);
        obj.globalData.code = res.code;
        getUserSetting(obj, backend, func, parm)
      },
      fail: res => {
        util.debug("login fail: ", res)
        util.info("Weixin server problem. Failed to login.")
        wx.reLaunch({
          url: '/pages/prelogin/prelogin?info=weixin',
        })
      }
    })
}

module.exports = {
  weixinUserLogin: weixinUserLogin,
  postRequest: postRequest,
  getRequest: getRequest,
  getUserInfo: getUserInfo
}