import 'package:fanji/tab/TabItem.dart';
import 'package:fanji/tab/TabLayout.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'Recommend.dart';

class Home extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _HomeState();
  }
}

class _HomeState extends State<Home> implements OnTabClickListener {
  List<String> _titles = ["关注", "推荐", "发现"];
  List<Widget> _childs = List.generate(3, (index) => Recommend("the jsd!"));

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: EdgeInsets.fromLTRB(0, 50, 0, 0),
        child: TabLayout(
          items: new List.generate(_childs.length,
              (index) => TabItem(_titles[index], _childs[index])),
          onTabClickListener: this,
        ),
      ),
    );
  }

  var i = 0;
  @override
  onTap(int index) {
    i++;
    print("index:" + index.toString() + " | i:" + i.toString());
  }
}
