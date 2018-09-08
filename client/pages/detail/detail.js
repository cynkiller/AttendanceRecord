// pages/detail/detail.js
const app = getApp()
const util = require('../../utils/util.js')
const request = require('../../utils/request.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),  
    
    // mock data
    mapping: {
      '准时': 0,
      '请假': 6,
      '迟到': 2,
      '缺席': 12
    },
    point: 30,
    rehearsalRecord: [],
    /*
      {
        date: "2018-06-02",
        status: '请假',
        remainPoint: 22
      },
      {
        date: "2018-05-26",
        status: '迟到',
        remainPoint: 28        
      },
      {
        date: "2018-05-19",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-12",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-05",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-28",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-21",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-19",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-12",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-05",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-28",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-21",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-19",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-12",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-05-05",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-28",
        status: '准时',
        remainPoint: 30
      },
      {
        date: "2018-04-21",
        status: '准时',
        remainPoint: 30
      }
    ],
    */
    maxIdx: 6
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    util.onloadCheck(app, this)
    var maxIdx = 6
    this.setData({
      rehearsalRecord: this.data.rehearsalRecord,
      maxIdx: maxIdx
    })
    if (app.backendUser) {
      this.setUserInfo(this, app.backendUser)
    } else {
      request.getRequest("/queryUserInfo", request.getUserInfo, this.setUserInfo, this);
    }
    request.getRequest('/queryRecords', this.queryRecordsCallback, null, this);
  },

  setUserInfo: function (obj, data) {
    util.info("enter setUserInfo")
    if (data.hasOwnProperty("data")) data = data.data;
    obj.setData({
      point: data.point,
      nickName: data.nickName,
      authority: data.authority
    })
  },

  queryRecordsCallback: function(data, callback, obj) {
    util.info("enter queryRecordsCallback")
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
      var rehearsalRecord = [];
      var records = data.data;
      var rehearsals = data.rehearsal;
      for (var i = records.length - 1; i >= 0; i--) {
        if (records[i].remainPoint == 99999) {
          util.debug("Skip ongoing rehearsal record")
          continue
        }
        var record = {};
        record['remainPoint'] = records[i].remainPoint;
        for (var j = 0; j < rehearsals.length; j++) {
          if (rehearsals[j].id == records[i].rehearsalId) {
            record['date'] = rehearsals[j].date;
            break;
          }
        }
        if (records[i].attendance == 'LATE') {
          record['status'] = "迟到"
        } else if (records[i].attendance == 'ASK_LEAVE') {
          record['status'] = "请假"
        } else if (records[i].attendance == 'ABSENCE') {
          record['status'] == "缺席"
        } else if (records[i].attendance == 'ON_TIME') {
          record['status'] == "准时"
        }
        rehearsalRecord.push(record)
      }
      obj.setData({
        rehearsalRecord: rehearsalRecord
      })
    }
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
  /*onShareAppMessage: function () {
  
  }*/
  loadMore: function() {
    var maxIdx = 2 * this.data.maxIdx;
    this.setData({
      maxIdx: maxIdx
    })
  },
  fold: function() {
    this.setData({
      maxIdx: 6
    })
  }
})