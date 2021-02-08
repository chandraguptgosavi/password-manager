import 'dart:async';

import 'package:flutter/services.dart';
import 'package:password_manager/models/user_account.dart';
import 'package:password_manager/values/strings.dart';

class Bloc {
  List _accounts = [];
  final _accountsController = StreamController();
  Stream get accountsStream => _accountsController.stream;
  static const _platform =
      const MethodChannel('com.flutter.password_manager/communication');

    String validator(String value) {
    var regex = RegExp(r"^[0-9]+$");
    var isInt = regex.hasMatch(value.trim());
    if (value.isEmpty) {
      return Strings.code_required;
    } else if (isInt == false) {
      return Strings.should_number;
    } else if (value.trim().length != 4) {
      return Strings.should_four_digit;
    }
    return null;
  }

  Future<void> init() async {
    await _loadAccounts();
  }

  Future<void> _loadAccounts() async {
    try {
      var mapList = await _platform.invokeMethod('loadAccounts');
      _accounts = mapList
          .map(
            (item) => Account(
                id: item["id"],
                title: item["title"],
                username: item["username"],
                password: item["password"],
                packageName: item["package_name"]),
          )
          .toList();
      _accountsController.add(_accounts);
    } on PlatformException catch (e) {
      _printException('_loadAccounts', e);
    }
  }

  Future<void> save(
      String title, String username, String password, String package) async {
    var account = Account(
      title: title,
      username: username,
      password: password,
      packageName: package,
    );
    await _addAccount(account);
  }

  Future<void> _addAccount(Account account) async {
    try {
      await _platform.invokeMethod('addAccount', {
        'title': account.title,
        'username': account.username,
        'password': account.password,
        'package_name': account.packageName,
      });
      await _loadAccounts();
    } on PlatformException catch (e) {
      _printException('_addAccount', e);
    }
  }

  Future<void> deleteAccount(Account account) async {
    try {
      await _platform.invokeMethod('deleteAccount', {
        'id': account.id,
        'title': account.title,
        'username': account.username,
        'password': account.password,
        'package_name': account.packageName,
      });
      await _loadAccounts();
    } on PlatformException catch (e) {
      _printException('deleteAccount', e);
    }
  }

  Future<void> enableAutofillSuccessfulWindow() async {
    try {
      await _platform.invokeMethod('enableDrawOverOtherApps');
    } on PlatformException catch (e) {
      _printException('enableAutofillSuccessfulWindow', e);
    }
  }

  void _printException(String method, PlatformException e) {
    print('Platform Exception in $method: ${e.message}');
  }

  void dispose() {
    _accountsController.close();
  }
}
