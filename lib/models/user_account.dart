import 'package:flutter/foundation.dart';

class Account {
  int id;
  String title;
  String username;
  String password;
  String packageName;
  Account({
    this.id,
    @required this.title,
    @required this.username,
    @required this.password,
    this.packageName,
  });
}
