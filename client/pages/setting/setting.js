// pages/setting/setting.js

const app = getApp()
const util = require('../../utils/util.js')
const rehearsal = require('../../utils/rehearsal.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    // TBD: 从数据库读取
    // addressBook: []
    settingStatus: {
      settingAddress: false,
      settingDate: false,
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
    ]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log(this.data.addressBook);
    this.setData({
      rehearsalDate: app.rehearsalInfo.rehearsalDate,
      readableDate: util.toReadableDate(app.rehearsalInfo.rehearsalDate.date),
      rehearsalLocation: app.rehearsalInfo.rehearsalLocation,
      addressBook: app.rehearsalInfo.addressBook
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
        console.log(res, "location")
        console.log(res.name)
        console.log(res.address)
        console.log(res.latitude)
        console.log(res.longitude)

        var address = {};
        address['name'] = res.name;
        address['address'] = res.address;
        address['latitude'] = res.latitude;
        address['longitude'] = res.longitude;
        address['checked'] = false;

        app.rehearsalInfo.addressBook.push(address);
        console.log(app.rehearsalInfo.addressBook)
        that.setData({
          addressBook: app.rehearsalInfo.addressBook
        })
        // TBD: Update to backend
        /* should be moved to update address
        that.setData({
          rehearsalLocation: res.name + ' ' + res.address
        })
        app.rehearsalInfo.rehearsalLocation = res.name + ' ' + res.address;
        app.rehearsalInfo.latitude = res.latitude;
        app.rehearsalInfo.longitude = res.longitude;
        */
      },
      fail: function () {
        // fail
      },
      complete: function () {
        // complete
      }
    })
    console.log(app.globalData.userInfo.nickName)
    console.log(app.globalData.userInfo.avatarUrl)
    console.log("global addressBook", app.rehearsalInfo.addressBook)

/*
    // Test https request
    wx.request({
      url: 'http://ec2-54-244-201-130.us-west-2.compute.amazonaws.com/api/people',
      header: {
        'content-type': 'application/json',
        'Accept': 'application/json'
      },
      method: "GET",
      success: function (res) {
        //console.log(res.data._links.people.href)
        console.log("jsonmsg", res.data)
        that.setData({
          //jsonmsg: res.data._links.people.href
          jsonmsg: res.data
        })
      },
      fail: function (res) {
        console.log("request failed.")
        console.log(res)
      },
      complete: function () {
        console.log("request complete.")
      }
    })
*/
  },

  /* 弹出/隐藏设置panel */
  settingStatus: function(event) {
    var status = event.currentTarget.dataset.status;
    var item = event.currentTarget.dataset.item;
    status = (status == true) ? false : true; // 翻转弹出/隐藏状态
    var setting = this.data.settingStatus;
    setting[item] = status;
    this.setData({
      settingStatus: setting
    })
  },

  /* 更新排练地址 */
  updateAddress: function( event ) {
    console.log('radio发生change事件，携带value值为：', event.detail.value);

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
      rehearsalLocation: addressBook[addressIdx].name + ' ' + addressBook[addressIdx].address
    });
    app.rehearsalInfo.rehearsalLocation = addressBook[addressIdx].name + ' ' + addressBook[addressIdx].address;
    app.rehearsalInfo.latitude = addressBook[addressIdx].latitude;
    app.rehearsalInfo.longitude = addressBook[addressIdx].longitude;
  },

  /* 更新排练日期 */
  bindDateChange: function( event ) {
    console.log('date发生change事件，携带value值为：', event.detail.value);
    app.rehearsalInfo.rehearsalDate.date = event.detail.value;
    var rehearsalDate = this.data.rehearsalDate;
    rehearsalDate.date = event.detail.value;
    this.setData({
      rehearsalDate: rehearsalDate,
      readableDate: util.toReadableDate(rehearsalDate.date)
    })
  },

  /* 更新开始排练时间 */
  bindStartTimeChange: function( event ) {
    console.log('startTime发生change事件，携带value值为：', event.detail.value);
    app.rehearsalInfo.rehearsalDate.startTime = event.detail.value;
    var rehearsalDate = this.data.rehearsalDate;
    rehearsalDate.startTime = event.detail.value;
    this.setData({
      rehearsalDate: rehearsalDate
    })
  },

  /* 更新结束排练时间 */
  bindEndTimeChange: function (event) {
    console.log('endTime发生change事件，携带value值为：', event.detail.value);
    app.rehearsalInfo.rehearsalDate.endTime = event.detail.value;
    var rehearsalDate = this.data.rehearsalDate;
    rehearsalDate.endTime = event.detail.value;
    this.setData({
      rehearsalDate: rehearsalDate
    })
  },

  addAdministrator: function (event) {
    console.log('addAdministrator发生change事件，携带idx值为：', event.currentTarget.dataset.index);
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
    console.log('removeAdministrator发生change事件，携带index值为：', event.currentTarget.dataset.index);
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
  }
})