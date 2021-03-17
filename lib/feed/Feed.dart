import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:toast/toast.dart';

class Feed extends StatefulWidget {
  String title;
  Feed(this.title);

  @override
  State<StatefulWidget> createState() => _FeedState();
}

class _FeedState extends State<Feed> {
  bool isLoadingImg = true;
  String imgUrl =
      "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2747439697,196176810&fm=26&gp=0.jpg";

  Future onRefresh() {
    return Future.delayed(Duration(seconds: 1), () {
      Toast.show("当前已是最新数据！", context);
    });
  }

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: this.onRefresh,
      child: Scaffold(
        body: Padding(
          padding: EdgeInsets.fromLTRB(0, 0, 0, 0),
          child: _buildListView(),
        ),
      ),
    );
  }

  ListView _buildListView() {
    return ListView.separated(
        itemBuilder: (BuildContext context, int index) {
          if (isLoadingImg) {
            return Image.network(
              imgUrl,
              fit: BoxFit.cover,
            );
          } else {
            return Container(
              child: Text("加载中..."),
            );
          }
        },
        separatorBuilder: (context, index) {
          return Divider(
            height: .5,
            indent: 75,
            color: Colors.red,
          );
        },
        itemCount: 100);
  }
}
