// pages/password/password.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    badpassword: false,
    // mock data
    goodPassword: '****'
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
  formSubmit: function( event ) {
    console.log(event.detail.value.passwd)
    if (this.data.goodPassword == event.detail.value.passwd) {
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
  }
})
