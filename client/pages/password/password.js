
const request = require('../../utils/request.js')
// pages/password/password.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    badpassword: false
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

  },

  formSubmit: function( event ) {
    var userpasswd = event.detail.value.passwd;
    // get way not safe!
    request.getRequest("/test/getSecretWord", this.passwordCallback, userpasswd)
    // post way
    /*
    var sendData = wx.getStorageSync('sessionData');
    sendData['secretWord'] = userpasswd;
    postRequest("/test/verifyLogin", sendData, verifyLoginCallback, null);
    */
  }
})
