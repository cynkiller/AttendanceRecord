// pages/home/home.js

//获取应用实例
const app = getApp()
const util = require('../../utils/util.js')
const req = require('../../utils/request.js')
const rehearsal = require('../../utils/rehearsal.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showpage: false,
    disableSignin: false,
    signined: false,
    distance: null,
    rehearsalStarted: false,
    remainMinutes: null,
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    markers: [
      {
        id: 0,
        iconPath: "/resource/position.png",
        longitude: null,
        latitude: null,
        width: 30,
        height: 30,
      }, // destination
      {}  // current location    
    ],
    includePoints: [
      {
        longitude: null,
        latitude: null,
      },
      {}
    ],
    interval: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    wx.showShareMenu({
      withShareTicket: true //要求小程序返回分享目标信息
    })

    req.weixinUserLogin(app, true, this.showpage)


    // 地理位置权限
    wx.authorize({
      scope: "scope.userLocation",
      success: function (res) {
        util.debug("userLocation success", res.errMsg)
      },
      fail: function (res) {
        util.debug("userLocation fail", res.errMsg)
      },
      complete: function (res) {
        util.debug("userLocation complete", res.errMsg)
      }
    })

    wx.getSetting({
      success: function (res) {
        if (!res.authSetting['scope.userLocation']) {
          wx.showToast({
            title: '小程序需要地理位置权限'
          })
          wx.openSetting({
            success: function (res) {
              if (!res.authSetting['scope.userLocation']) {
                wx.showToast({
                  title: '获取地理位置失败',
                })
                // 设置flag不要获取地理位置
              }
            }
          })
        }
      }
    })

  },

  checkPunchStatusCallback: function(data, callback, obj) {
    util.info("enter checkPunchStatusCallback")
    if (!data.status) {
      util.info("Remote backend problem. Failed to change userinfo.")
      obj.setData({
        updatefail: true,
        failmsg: "无法连接服务器。。更新失败"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "SERVER_SESSION_EXPIRED") {
      util.info("Login session expired.")
      app.loginReady = false;
      obj.setData({
        updatefail: true,
        failmsg: "重新登陆中。。"
      })
      // relogin
      req.weixinUserLogin(app, true, function (obj) {
        obj.setData({ updatefail: false })
      }, obj)
    } else if (data.status == "SERVER_INTERNAL_ERROR") {
      obj.setData({
        updatefail: true,
        failmsg: "程序出了个bug！0.0"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "GENERAL_OK") {
      util.info("Update successful.")
      if (data.data == "PUNCHED") {
        obj.setData({
          disableSignin: true,
          signined: true
        })
        wx.showToast({
          title: '已签到',
        })
      }
    }
  },

  checkPunchStatus: function() {
    // check if already signined
    req.getRequest("/queryPunchStatus", this.checkPunchStatusCallback, null, this)
  },  

  showpage: function() {
    util.info("showpage")
    this.setData({
      showpage: true
    })
    this.onShow()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    
  },

  onPullDownRefresh: function() {
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    if(this.data.showpage) {
      this.onShowOperation();
      if(this.data.interval !== undefined) {
        // Refresh location every 60 seconds
        this.data.interval = setInterval(this.onShowOperation, 60000);
      }
      this.checkPunchStatus()
    }
  },

  rehearsalInfoCallback: function(obj) {
    this.data.markers[0].longitude = app.rehearsalInfo.address.longtitude;
    this.data.markers[0].latitude = app.rehearsalInfo.address.latitude;
    this.data.includePoints[0].longitude = app.rehearsalInfo.address.longtitude;
    this.data.includePoints[0].latitude = app.rehearsalInfo.address.latitude;
    this.setData({
      rehearsalDate: app.rehearsalInfo.rehearsalDate,
      readableDate: util.toReadableDate(app.rehearsalInfo.rehearsalDate.date),
      rehearsalLocation: app.rehearsalInfo.address.address + " " + app.rehearsalInfo.address.location,
      markers: this.data.markers,
      includePoints: this.data.includePoints
    })
    util.debug(this.data.markers);
    util.getCurrentPosition(this);
    var distance = util.getGpsDisance(
      this.data.includePoints[0].latitude,
      this.data.includePoints[0].longitude,
      this.data.includePoints[1].latitude,
      this.data.includePoints[1].longitude);
    util.debug("distance:", distance)
    var remain = rehearsal.getRemainTime(this);
    //if (rehearsal.isValidSigninTime(this) && this.data.signined == false && distance <= 10) {
    if (rehearsal.isValidSigninTime(this) && this.data.signined == false) { // mock distance valid
      remain = (remain / 1000 / 60 / 60).toFixed(2);
      this.setData({
        disableSignin: false,
        remainHours: remain
      })
    } else {
      if (remain != null)
        remain = (remain / 1000 / 60 / 60).toFixed(2);
      this.setData({
        disableSignin: true,
        remainHours: remain
      })
      if (!rehearsal.isValidSigninTime(this)) {
        wx.showToast({
          title: '不在打卡时间内',
          duration: 2000
        })
      } else if (distance > 10) {
        wx.showToast({
          title: '不在打卡距离内',
          duration: 2000
        })
      }
    }
  },

  onShowOperation() {

    // get rehearsal info from backend
    if (app.rehearsalInfo.address.location == null)
      rehearsal.getNextRehearsal(this, this.rehearsalInfoCallback)
    else
      this.rehearsalInfoCallback();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    // 停止polling地理位置和时间
    if(this.data.showpage) {
        util.debug("clearIntervfal", this.data.interval)
        clearInterval(this.data.interval);  
    }
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
  onShareAppMessage: function () {
    return {
      title: '打卡神器',
      path: "/pages/home/home"
    }
  },

  /*********************
   * Customized functions
   *********************/

  signinSuccessCallback: function(obj, info) {
    // 更新后台信息
    util.debug(info)
    if (info == "ON_TIME") {
      wx.showToast({
        title: '准时签到！',
      })
    } else if (info == "LATE") {
      wx.showToast({
        title: '迟到!',
      })
    }
    obj.setData({
      disableSignin: true,
      signined: true
    })
  },

  signinCallback: function(data, callback, obj) {
    util.info("enter signinCallback")
    if (!data.status) {
      util.info("Remote backend problem. Failed to change userinfo.")
      obj.setData({
        updatefail: true,
        failmsg: "无法连接服务器。。更新失败"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "SERVER_SESSION_EXPIRED") {
      util.info("Login session expired.")
      app.loginReady = false;
      obj.setData({
        updatefail: true,
        failmsg: "重新登陆中。。"
      })
      // relogin
      req.weixinUserLogin(app, true, function (obj) {
        obj.setData({ updatefail: false })
      }, obj)
    } else if (data.status == "SERVER_REHEARSAL_NOT_STARTED") {
      util.info("排练打卡时间还没到")
      obj.setData({
        updatefail: true,
        failmsg: "排练打卡时间还没到"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "SERVER_PUNCHIN_TIME_PASSED") {
      util.info("打卡时间已经过了0.0")
      obj.setData({
        updatefail: true,
        failmsg: "打卡时间已经过了0.0"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)      
    } else if (data.status == "SERVER_UPDATE_REHEARSAL_STATUS_FAILED") {
      util.info("打卡失败")
      obj.setData({
        updatefail: true,
        failmsg: "打卡失败"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)      
    } else if (data.status == "SERVER_INTERNAL_ERROR") {
      obj.setData({
        updatefail: true,
        failmsg: "程序出了个bug！0.0"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "GENERAL_OK") {
      util.info("Update successful.")
      if (callback) callback(obj, data.data)
    }
  },

  signin: function() {
    util.getCurrentPosition(this);
    var distance = util.getGpsDisance(
      this.data.includePoints[0].latitude,
      this.data.includePoints[0].longitude,
      this.data.includePoints[1].latitude,
      this.data.includePoints[1].longitude);
    util.debug("distance:", distance)
    //if (rehearsal.isValidSigninTime(this) && distance <= 10) {
    if (rehearsal.isValidSigninTime(this)) { // mock distance valid
      req.postRequest("/punchIn", {}, this.signinCallback, this.signinSuccessCallback, this)
    }
  }
})