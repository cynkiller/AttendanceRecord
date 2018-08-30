// pages/setting/setting.js

const app = getApp()
const util = require('../../utils/util.js')
const rehearsal = require('../../utils/rehearsal.js')
const request = require('../../utils/request.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    settingStatus: {
      settingRehearsal: false,
      managingAdministrator: false,
      modifyPoint: false
    },

    // mock data
    administrators : [
      { 
        id: 1,
        name: '倍儿帅',
        avatarurl: '/resource/meilin.jpg',
      },
      {
        id: 2,
        name: "台风哥",
        avatarurl: '/resource/weui/pic_160.png'
      }
    ],
    adminCandidate: [
      {
        id: 1,
        name: '梅林',
        avatarurl: '/resource/meilin.jpg',
        checked: false
      },
      {
        id: 2,
        name: "莫德雷德",
        avatarurl: '/resource/weui/pic_160.png',
        checked: false
      }
    ],
    strategies: [],
    addressBook: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    util.debug(this.data.addressBook);
    // fake data
    var strtgs = []
    for (var i = 0; i < 60; i++) {
      strtgs.push(i - 30)
    }

    request.getRequest("/admin/queryAddress", this.addressCallback, this.queryAddressCallback, this);

    this.setData({
      rehearsalDate: app.rehearsalInfo.rehearsalDate,
      readableDate: util.toReadableDate(app.rehearsalInfo.rehearsalDate.date),
      rehearsalLocation: app.rehearsalInfo.address.address + " " + app.rehearsalInfo.address.location,
      mod_event: app.rehearsalInfo.rehearsalDate.event,
      mod_date: app.rehearsalInfo.rehearsalDate.date,
      mod_startTime: app.rehearsalInfo.rehearsalDate.startTime,
      mod_endTime: app.rehearsalInfo.rehearsalDate.endTime,
      mod_isHoliday: app.rehearsalInfo.rehearsalDate.isHoliday,
      strategies: strtgs,
      strategyIndex: 30
    })
  },

  addressCallback: function(data, callback, obj) {
    util.info("enter addressCallback")
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
      request.weixinUserLogin(app, true, function (obj) {
        obj.setData({ updatefail: false })
      }, obj)
    } else if (data.status == "CLIENT_NOT_AUTHORIZED") {
      obj.setData({
        updatefail: true,
        failmsg: "Oops。。没有权限哦>-<"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "SERVER_ADDRESS_EXIST") {
      obj.setData({
        updatefail: true,
        failmsg: "地址已存在。。不用再加啦"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "SERVER_ADDRESS_NOT_EXIST") {
      obj.setData({
        updatefail: true,
        failmsg: "地址不存在。。"
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
      if (callback) callback(data, obj)
    }
  },

  queryAddressCallback: function(data, obj) {
    util.debug(data.data)
    util.info("enter queryAddressCallback")
    if (data.hasOwnProperty("data")) data = data.data;
    for (var i = 0, len = data.length; i < len; ++i) {
      if (data[i].addrId == app.rehearsalInfo.rehearsalDate.addrId) {
        data[i].checked = 1;
        break;
      }
    }
    obj.setData({
      addressBook: data,
      mod_addrId: data[i].addrId
    })
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
    if (app.backendUser) {
      this.setUserInfo(this, app.backendUser)
    } else {
      request.getRequest("/queryUserInfo", request.getUserInfo, this.setUserInfo, this);
    }
  },

  setUserInfo: function (obj, data) {
    util.info("enter setUserInfo")
    if (data.hasOwnProperty("data")) data = data.data;
    obj.setData({
      authority: data.authority
    })
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
    request.getRequest("/queryUserInfo", request.getUserInfo, this.setUserInfo, this);
    request.getRequest("/admin/queryAddress", this.addressCallback, this.queryAddressCallback, this);
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
  
  },

  /**
   * 用户点击右上角分享
   */
  /* disable share in this page
  onShareAppMessage: function () {
  
  },
  */


  /**
   * Customized functions
   */
  selectLocation: function () {
    var that = this;
    wx.chooseLocation({
      success: function (res) {
        // success
        util.debug(res, "location")
        util.debug(res.name)
        util.debug(res.address)
        util.debug(res.latitude)
        util.debug(res.longitude)

        var address = {};
        address['location'] = res.name;
        address['address'] = res.address;
        address['latitude'] = res.latitude;
        address['longitude'] = res.longitude;

        // Update backend database
        request.postRequest("/admin/addNewAddress", address, that.addressCallback, that.addAddressCallback, that);
      },
      fail: function () {
        // fail
      },
      complete: function () {
        // complete
      }
    })
  },

  addAddressCallback: function(data, obj) {
    util.info("enter addAddressCallback")
    request.getRequest("/admin/queryAddress", this.addressCallback, this.queryAddressCallback, this);
    wx.showToast({
      title: '添加地址成功',
    })
  },

  /* 弹出/隐藏设置panel */
  settingStatus: function(event) {
    var status = event.currentTarget.dataset.status;
    var item = event.currentTarget.dataset.item;
    status = (status == true) ? false : true; // 翻转弹出/隐藏状态
    var setting = this.data.settingStatus;
    for(var key in setting) {
      setting[key] = false;
    }
    setting[item] = status;
    this.setData({
      settingStatus: setting
    })
  },

  /* 更新排练地址 */
  updateAddress: function( event ) {
    util.debug('radio发生change事件，携带value值为：', event.detail.value);

    var selectAddress = event.detail.value;
    var addressBook = this.data.addressBook;
    var addressIdx = null;
    for (var i = 0, len = addressBook.length; i < len; ++i) {
      addressBook[i].checked = addressBook[i].address == selectAddress;
      if (addressBook[i].checked == 1) {
        addressIdx = i;
      }
    }

    this.setData({
      addressBook: addressBook,
      mod_addrId: addressBook[addressIdx].addrId
      //rehearsalLocation: addressBook[addressIdx].location + ' ' + addressBook[addressIdx].address
    });
    //app.rehearsalInfo.rehearsalLocation = addressBook[addressIdx].location + ' ' + addressBook[addressIdx].address;
    //app.rehearsalInfo.latitude = addressBook[addressIdx].latitude;
    //app.rehearsalInfo.longitude = addressBook[addressIdx].longitude;
  },

  removeAddress: function(event) {
    util.debug("removeAddress index", event.target.dataset.index)
    // TBD
    var index = event.target.dataset.index;
    var sendData = {}
    sendData["longitude"] = this.data.addressBook[index].longtitude;
    sendData["latitude"] = this.data.addressBook[index].latitude;
    request.postRequest("/admin/removeNewAddress", sendData, this.addressCallback, this.removeAddressCallback, this);
  },

  removeAddressCallback: function(data, obj) {
    util.info("enter userinfo removeAddressCallback")
    request.getRequest("/admin/queryAddress", this.addressCallback, this.queryAddressCallback, this);
    wx.showToast({
      title: '删除地址成功',
    })
  },

  /* 更新排练日期 */
  bindDateChange: function( event ) {
    util.debug('date发生change事件，携带value值为：', event.detail.value);
    //app.rehearsalInfo.rehearsalDate.date = event.detail.value;
    //var rehearsalDate = this.data.rehearsalDate;
    //rehearsalDate.date = event.detail.value;
    this.setData({
      mod_date: event.detail.value
      //rehearsalDate: rehearsalDate,
      //readableDate: util.toReadableDate(rehearsalDate.date)
    })
  },

  /* 更新开始排练时间 */
  bindStartTimeChange: function( event ) {
    util.debug('startTime发生change事件，携带value值为：', event.detail.value);
    //app.rehearsalInfo.rehearsalDate.startTime = event.detail.value;
    //var rehearsalDate = this.data.rehearsalDate;
    //rehearsalDate.startTime = event.detail.value;
    this.setData({
      mod_startTime: event.detail.value
    })
  },

  /* 更新结束排练时间 */
  bindEndTimeChange: function (event) {
    util.debug('endTime发生change事件，携带value值为：', event.detail.value);
    //app.rehearsalInfo.rehearsalDate.endTime = event.detail.value;
    //var rehearsalDate = this.data.rehearsalDate;
    //rehearsalDate.endTime = event.detail.value;
    this.setData({
      mod_endTime: event.detail.value
    })
  },

  isHolidayChange: function (event) {
    util.debug('isHolidayChange发生change事件，携带value值为：', event.detail.value);
    this.setData({
      mod_isHoliday: event.detail.value
    })
  },

  updateRehearsalDone: function(obj) {
    var status = this.data.settingStatus
    status.settingRehearsal = false;
    obj.setData({
      rehearsalDate: app.rehearsalInfo.rehearsalDate,
      readableDate: util.toReadableDate(app.rehearsalInfo.rehearsalDate.date),
      rehearsalLocation: app.rehearsalInfo.address.address + " " + app.rehearsalInfo.address.location,
      settingStatus: status
    })
    wx.showToast({
      title: '更新成功',
    })
  },

  updateRehearsalCallback: function( data, callback, obj ) {
    util.info("enter updateRehearsalCallback")
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
      request.weixinUserLogin(app, true, function (obj) {
        obj.setData({ updatefail: false })
      }, obj)
    } else if (data.status == "SERVER_INTERNAL_ERROR") {
      util.info("Server internal error.")
      // TBD
    } else if (data.status == "SERVER_REHEARSAL_UPDATE_FAIL") {
      obj.setData({
        updatefail: true,
        failmsg: "排练信息更新失败"
      })
      setTimeout(function (obj) {
        obj.setData({
          updatefail: false
        })
      }, 3000, obj)
    } else if (data.status == "GENERAL_OK") {
      util.info("Update successful.")
      rehearsal.getNextRehearsal(this, this.updateRehearsalDone)
    }
  },

  updateRehearsal: function( event) {
    util.debug('updateRehearsal，携带value值为：', event.detail.value);
    var event = event.detail.value.event;
    if (event == "") {
      event = this.data.mod_event
    }
    util.debug(event, this.data.mod_addrId, this.data.mod_date, this.data.mod_startTime, this.data.mod_endTime, this.data.mod_isHoliday, app.rehearsalInfo.rehearsalDate.state)
    var startTimestamp = rehearsal.toTimestamp(this.data.mod_date, this.data.mod_startTime)
    var endTimestamp = rehearsal.toTimestamp(this.data.mod_date, this.data.mod_endTime)
    util.debug(startTimestamp, endTimestamp)
    util.debug(rehearsal.timestampToTime(startTimestamp), rehearsal.timestampToTime(endTimestamp))
    var sendData = {}
    sendData['id'] = app.rehearsalInfo.rehearsalDate.id;
    sendData['date'] = this.data.mod_date;
    sendData['startTimestamp'] = startTimestamp;
    sendData['endTimestamp'] = endTimestamp;
    sendData['isHoliday'] = this.data.mod_isHoliday;
    sendData['event'] = event;
    sendData['addrId'] = this.data.mod_addrId;
    request.postRequest('/setRehearsalInfo', sendData, this.updateRehearsalCallback, this) // TBD: callbacks
  },

  addAdministrator: function (event) {
    util.debug('addAdministrator发生change事件，携带idx值为：', event.currentTarget.dataset.index);
    var idx = event.currentTarget.dataset.index;
    var admins = this.data.administrators;
    var candidates = this.data.adminCandidate;
    var admin = {};
    admin.id = candidates[idx].id;
    admin.name = candidates[idx].name;
    admin.avatarurl = candidates[idx].avatarurl;

    // remove from candidate list
    candidates.splice(idx, 1);
  
    // add to admin list
    admins.push(admin);

    this.setData({
      adminCandidate: candidates,
      administrators: admins
    })
  },
  removeAdministrator: function (event) {
    util.debug('removeAdministrator发生change事件，携带index值为：', event.currentTarget.dataset.index);
    var idx = event.currentTarget.dataset.index;
    var admins = this.data.administrators;
    var candidate = admins[idx];
    candidate.checked = false;

    // remove from admin
    admins.splice(idx, 1)

    // add to candidate
    var candidates = this.data.adminCandidate;
    candidates.push(candidate)

    this.setData({
      administrators: admins,
      adminCandidate: candidates
    })
  },

  confirmSetting: function( event ) {
    // TBD: Update database
    // 接口： 新增管理员id名单，移除管理员id名单
    wx.showToast({
      title: '变更成功！',
    })
    util.sleep(2000);
    var setting = this.data.settingStatus;
    setting['managingAdministrator'] = false;
    this.setData({
      settingStatus: setting
    })
  },

  selectStrategy: function( event ) {
    util.debug('selectStrategy发生change事件，携带strategyIndex值为：', event.detail.value);
    var idx = event.detail.value;
    this.setData({
      strategyIndex: idx
    })
  },

  setNewSecretWord: function( event ) {
    util.debug(event.detail.value.newpasswd)
    var sendData = {}
    sendData['secretWord'] = event.detail.value.newpasswd;
    request.postRequest("/admin/setSecretWord", sendData);
    // TBD: callback func
  }
})