import 'package:fanji/tab/TabItem.dart';
import 'package:fanji/tab/TabLayout.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Home extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _HomeState();
  }
}

class _HomeState extends State<Home> implements OnTabClickListener {
  bool isLoadingImg = true;
  String imgUrl =
      "http://zd112.oss-cn-beijing.aliyuncs.com/jpg/compress_mmexport1538795397848.jpg";

  List<String> _title = ["关注", "推荐", "发现", "科技"];
  List<Widget> _childs = [
    Center(child: Text("新闻")),
    Center(
      child: Text("音乐"),
    ),
    Center(
      child: Text("军事"),
    ),
    Center(
      child: Text("科技"),
    )
  ];

  List<TabItem> _buildTabItems(List<String> titles, List<Widget> items) {
    var list = List<TabItem>();
    for (var i = 0; i < items.length; i++) {
      var item = TabItem();
      item.child = items[i];
      item.title = titles[i];
      list.add(item);
    }
    return list;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: EdgeInsets.fromLTRB(0, 50, 0, 0),
        child: TabLayout(
          items: _buildTabItems(_title, _childs),
          onTabClickListener: this,
        ),
      ),
    );
  }

  ListView buildListView() {
    return ListView.separated(
        itemBuilder: (BuildContext context, int index) {
          if (isLoadingImg) {
            return Image.network(
              imgUrl,
              height: 100,
              fit: BoxFit.fitHeight,
            );
          } else {
            return Container(
              child: Text("加载中..."),
            );
          }
        },
        separatorBuilder: (BuildContext context, int index) {
          return new Divider();
        },
        itemCount: 100);
  }

  var i = 0;
  @override
  onTap(int index) {
    i++;
    print("index:" + index.toString() + " | i:" + i.toString());
  }
}
