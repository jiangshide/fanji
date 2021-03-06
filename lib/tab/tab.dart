import 'package:fanji/channel/Channel.dart';
import 'package:fanji/message/Message.dart';
import 'package:fanji/publish/Publish.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../home/home.dart';
import '../mine/mine.dart';

class TabWidget extends StatefulWidget {
  TabWidget({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _TabWidgetState();
}

class _TabWidgetState extends State<TabWidget> {
  int _currentIndex = 0;
  List<Widget> _tabWidgets = [Home(), Channel(), Publish(), Message(), Mine()];

  List<String> _titles = ["Home", "Channel", "Publish", "Message", "Mine"];

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  // @override
  // Widget build(BuildContext context) {
  //   return Scaffold(
  //     body: _tabWidgets[_index],
  //     floatingActionButton: FloatingActionButton(
  //       onPressed: () {
  //         print("发布");
  //         Navigator.of(context).push(MaterialPageRoute(builder: (context) {
  //           return Publish(
  //             title: "new Pages",
  //           );
  //         }));
  //       },
  //       tooltip: "添加",
  //       child: Icon(
  //         Icons.add,
  //         color: Colors.white,
  //       ),
  //     ),
  //     floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
  //     bottomNavigationBar: BottomAppBar(
  //       color: Colors.lightBlue,
  //       shape: CircularNotchedRectangle(),
  //       child: Row(
  //         mainAxisSize: MainAxisSize.max,
  //         mainAxisAlignment: MainAxisAlignment.spaceAround,
  //         children: <Widget>[
  //           IconButton(
  //               icon: Icon(Icons.home),
  //               color: Colors.white,
  //               onPressed: () {
  //                 setState(() {
  //                   _index = 0;
  //                 });
  //               }),
  //           IconButton(
  //               icon: Icon(Icons.airport_shuttle),
  //               color: Colors.white,
  //               onPressed: () {
  //                 setState(() {
  //                   _index = 1;
  //                 });
  //               })
  //         ],
  //       ),
  //     ),
  //   );
  // }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_titles[this._currentIndex]),
      ),
      body: this._tabWidgets[this._currentIndex],
      // floatingActionButton: FloatingActionButton(
      //   onPressed: () {
      //     Navigator.of(context).push(MaterialPageRoute(builder: (context) {
      //       return Publish();
      //     }));
      //   },
      //   tooltip: "发布",
      //   child: Icon(
      //     Icons.add,
      //     color: Colors.white,
      //   ),
      // ),
      // floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: this._currentIndex,
        iconSize: 30.0,
        type: BottomNavigationBarType.fixed,
        onTap: (index) {
          setState(() {
            this._currentIndex = index;
          });
        },
        items: [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: '首页'),
          BottomNavigationBarItem(
            icon: Icon(Icons.track_changes),
            label: '频道',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.public),
            label: '发布',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.message),
            label: '消息',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.people_outline_rounded),
            label: '我的',
          ),
        ],
      ),
    );
  }
}
