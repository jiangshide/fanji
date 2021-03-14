import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

const FOOD_DATA = [
  {"name": "Burger", "brand": "Hawkers", "price": 2.99, "image": "burger.png"},
  {
    "name": "Cheese Dip",
    "brand": "Hawkers",
    "price": 4.99,
    "image": "cheese_dip.png"
  },
  {"name": "Cola", "brand": "Mcdonald", "price": 1.49, "image": "cola.png"},
  {"name": "Fries", "brand": "Mcdonald", "price": 2.99, "image": "fries.png"},
  {
    "name": "Ice Cream",
    "brand": "Ben & Jerry's",
    "price": 9.49,
    "image": "ice_cream.png"
  },
  {
    "name": "Noodles",
    "brand": "Hawkers",
    "price": 4.49,
    "image": "noodles.png"
  },
  {"name": "Pizza", "brand": "Dominos", "price": 17.99, "image": "pizza.png"},
  {
    "name": "Sandwich",
    "brand": "Hawkers",
    "price": 2.99,
    "image": "sandwich.png"
  },
  {"name": "Wrap", "brand": "Subway", "price": 6.99, "image": "wrap.png"},
  {"name": "Burger", "brand": "Hawkers", "price": 2.99, "image": "burger.png"},
  {
    "name": "Cheese Dip",
    "brand": "Hawkers",
    "price": 4.99,
    "image": "cheese_dip.png"
  },
  {"name": "Cola", "brand": "Mcdonald", "price": 1.49, "image": "cola.png"},
  {"name": "Fries", "brand": "Mcdonald", "price": 2.99, "image": "fries.png"},
  {
    "name": "Ice Cream",
    "brand": "Ben & Jerry's",
    "price": 9.49,
    "image": "ice_cream.png"
  },
  {
    "name": "Noodles",
    "brand": "Hawkers",
    "price": 4.49,
    "image": "noodles.png"
  },
  {"name": "Pizza", "brand": "Dominos", "price": 17.99, "image": "pizza.png"},
  {
    "name": "Sandwich",
    "brand": "Hawkers",
    "price": 2.99,
    "image": "sandwich.png"
  },
  {"name": "Wrap", "brand": "Subway", "price": 6.99, "image": "wrap.png"},
  {"name": "Burger", "brand": "Hawkers", "price": 2.99, "image": "burger.png"},
  {
    "name": "Cheese Dip",
    "brand": "Hawkers",
    "price": 4.99,
    "image": "cheese_dip.png"
  },
  {"name": "Cola", "brand": "Mcdonald", "price": 1.49, "image": "cola.png"},
  {"name": "Fries", "brand": "Mcdonald", "price": 2.99, "image": "fries.png"},
  {
    "name": "Ice Cream",
    "brand": "Ben & Jerry's",
    "price": 9.49,
    "image": "ice_cream.png"
  },
  {
    "name": "Noodles",
    "brand": "Hawkers",
    "price": 4.49,
    "image": "noodles.png"
  },
  {"name": "Pizza", "brand": "Dominos", "price": 17.99, "image": "pizza.png"},
  {
    "name": "Sandwich",
    "brand": "Hawkers",
    "price": 2.99,
    "image": "sandwich.png"
  },
  {"name": "Wrap", "brand": "Subway", "price": 6.99, "image": "wrap.png"}
];

class Datas {
  List<Widget> getPostsData() {
    List<dynamic> responseList = FOOD_DATA;
    List<Widget> listItems = [];
    responseList.forEach((post) {
      listItems.add(Container(
        height: 150,
        margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
        decoration: BoxDecoration(
            borderRadius: BorderRadius.all(Radius.circular(20.0)),
            color: Colors.white,
            boxShadow: [
              BoxShadow(color: Colors.black.withAlpha(100), blurRadius: 10.0),
            ]),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 10),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: <Widget>[
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Text(
                    post["name"],
                    style: const TextStyle(
                        fontSize: 28, fontWeight: FontWeight.bold),
                  ),
                  Text(
                    post["brand"],
                    style: const TextStyle(fontSize: 17, color: Colors.grey),
                  ),
                  SizedBox(
                    height: 10,
                  ),
                  Text(
                    "\$ ${post["price"]}",
                    style: const TextStyle(
                        fontSize: 25,
                        color: Colors.black,
                        fontWeight: FontWeight.bold),
                  ),
                ],
              ),
              Image.network(
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwww.leewiart.com%2Fuserfiles%2F4421%2F8db77019f697dc9c9df6df6efd0b9c01.jpg%3F634219776989282500&refer=http%3A%2F%2Fwww.leewiart.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1618310876&t=f7b5de6acc4d5c61c167e8a47d47c5ec",
                fit: BoxFit.cover,
              )
              // Image.asset(
              //   "assets/images/${post["image"]}",
              //   height: double.infinity,
              // ),
            ],
          ),
        ),
      ));
    });
    return listItems;
  }
}
