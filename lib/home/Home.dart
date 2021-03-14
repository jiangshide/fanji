import 'package:fanji/home/CusDrawer.dart';
import 'package:fanji/home/HomeDetail.dart';
import 'package:fanji/tab/TabItem.dart';
import 'package:fanji/tab/TabLayout.dart';
import 'package:fanji/view/Sidebar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'Recommend.dart';

class Home extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _HomeState();
  }
}

class _HomeState extends State<Home> {
  FSBStatus fsbStatus;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: Sidebar(
          drawerBackgroundColor: Colors.deepOrange,
          drawer: CusDrawer(
            closeDrawer: () {
              setState(() {
                fsbStatus = FSBStatus.FSB_CLOSE;
              });
            },
          ),
          screenContents: HomeContent(fsbStatus),
          status: fsbStatus,
        ),
        floatingActionButton: FloatingActionButton(
          backgroundColor: Colors.deepOrange,
          child: Icon(
            Icons.menu,
            color: Colors.white,
          ),
          onPressed: () {
            setState(() {
              fsbStatus = fsbStatus == FSBStatus.FSB_OPEN
                  ? FSBStatus.FSB_CLOSE
                  : FSBStatus.FSB_OPEN;
            });
          },
        ),
      ),
    );
  }
}

class HomeContent extends StatefulWidget {
  FSBStatus fsbStatus;

  HomeContent(this.fsbStatus);

  @override
  State<StatefulWidget> createState() => _HomeContentState(fsbStatus);
}

class _HomeContentState extends State<HomeContent>
    implements OnTabClickListener {
  FSBStatus fsbStatus;

  _HomeContentState(this.fsbStatus);

  List<String> _titles = ["最新", "最热"];
  List<Widget> _childs = [HomdeDetail(), Recommend("the jsd!")];
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: getAppBar(),
      body: TabLayout(
        items: List.generate(
            _childs.length, (index) => TabItem(_titles[index], _childs[index])),
        onTabClickListener: this,
      ),
    );
  }

  var i = 0;
  @override
  onTap(int index) {
    i++;
    print("index:" + index.toString() + " | i:" + i.toString());
  }

  AppBar getAppBar() {
    return AppBar(
      elevation: 0,
      backgroundColor: Colors.white,
      leading: IconButton(
        icon: Icon(
          Icons.menu,
          color: Colors.red,
        ),
        onPressed: () {
          fsbStatus = fsbStatus == FSBStatus.FSB_OPEN
              ? FSBStatus.FSB_CLOSE
              : FSBStatus.FSB_OPEN;
        },
      ),
      title: Text(
        "杰克",
        style: TextStyle(fontSize: 16, color: Colors.red),
      ),
      actions: [
        IconButton(
            icon: Icon(Icons.search, color: Colors.black), onPressed: () {}),
        IconButton(
            icon: Icon(
              Icons.person,
              color: Colors.black,
            ),
            onPressed: () {
              // setState(() {});
            })
      ],
    );
  }
}
