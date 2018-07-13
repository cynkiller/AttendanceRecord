
const app = getApp();
const request = require('../../utils/request.js');
const util = require('../../utils/util.js');

// pages/password/password.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    badpassword: false,
    connectionError: false,
    nopassword: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
  
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
  /*
  onShareAppMessage: function () {
  
  }
  */

  passwordCallback: function(data, userpasswd) {
      if (data == userpasswd) {
        wx.showToast({
          title: "梅林你可来了",
        })

        setTimeout(function () {
          wx.switchTab({
            url: '/pages/home/home',
          });
        }, 3000)

      } else {
        this.setData({
          badpassword: true
        })
        var that = this;
        setTimeout(function () {
          that.setData({
            badpassword: false
          });
        }, 1000);
      }
  },

  verifyLoginCallback: function(data, parm) {
    util.debug(data.status)
    wx.hideLoading()
    if (!data.status) {
      util.TempMessage(this, "connectionError");
    } else if (data.status == "GENERAL_OK") {
      wx.showToast({
        title: "梅林你可来了",
      })

      setTimeout(function () {
        wx.switchTab({
          url: '/pages/home/home',
        });
      }, 3000)
    } else if (data.status == "SERVER_NO_SECRETWORD") {
      util.TempMessage(this, "nopassword");
    } else if (data.status == "CLIENT_BAD_SECRETWORD") {
      util.TempMessage(this, "badpassword");
    } else if (data.status == "CLIENT_BAD_DATA") {
      // 重新登录
      request.weixinUserLogin(app);
    }
  },

  formSubmit: function( event ) {
    var userpasswd = event.detail.value.passwd;
    // get way not safe!
    //request.getRequest("/test/getSecretWord", this.passwordCallback, userpasswd)
    // post way

    // login again, because the previous session code is out-dated for backend query
    request.weixinUserLogin(app);
    var sessionData = wx.getStorageSync('sessionData');
    //var sendData = {};
    //sendData['sessionData'] = sessionData;
    //sendData['secretWord'] = userpasswd;
    var sendData = sessionData;
    sendData['secretWord'] = userpasswd;
    util.debug(sendData);
    wx.showLoading({
      title: '身份验证中...'
    })
    request.postRequest("/admin/verifyLogin", sendData, this.verifyLoginCallback, null);
  }
})
