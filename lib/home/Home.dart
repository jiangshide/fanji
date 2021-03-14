import 'package:event_bus/event_bus.dart';
import 'package:fanji/home/CusDrawer.dart';
import 'package:fanji/home/HomeDetail.dart';
import 'package:fanji/tab/TabItem.dart';
import 'package:fanji/tab/TabLayout.dart';
import 'package:fanji/view/Sidebar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'Recommend.dart';

final eventBus = EventBus();

class Home extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _HomeState();
  }
}

class _HomeState extends State<Home> {
  FSBStatus fsbStatus;

  @override
  void initState() {
    super.initState();
    eventBus.on<FSBStatus>().listen((event) {
      setState(() {
        fsbStatus = fsbStatus == FSBStatus.FSB_OPEN
            ? FSBStatus.FSB_CLOSE
            : FSBStatus.FSB_OPEN;
      });
    });
  }

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
          screenContents: HomeContent(),
          status: fsbStatus,
        ),
      ),
    );
  }
}

class HomeContent extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _HomeContentState();
}

class _HomeContentState extends State<HomeContent>
    implements OnTabClickListener {
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
          eventBus.fire(FSBStatus.FSB_OPEN);
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
