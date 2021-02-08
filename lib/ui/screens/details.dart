import 'package:flutter/material.dart';
import 'package:password_manager/bloc/bloc.dart';
import 'package:password_manager/models/user_account.dart';
import 'package:password_manager/values/strings.dart';
import 'package:provider/provider.dart';

class DetailsPage extends StatefulWidget {
  final Account account;
  DetailsPage({this.account});

  @override
  _DetailsPageState createState() => _DetailsPageState();
}

class _DetailsPageState extends State<DetailsPage> {
  Bloc _bloc;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    _bloc = Provider.of<Bloc>(context, listen: false);
    var account = widget.account;
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          Strings.details_title,
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete_sharp),
            onPressed: () async {
              await _bloc.deleteAccount(account);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: const Text(Strings.deleted_successfully),
                ),
              );
              Navigator.of(context).pop();
            },
          )
        ],
      ),
      body: ListView(
        children: [
          ListTile(
            title: const Text(Strings.title),
            subtitle: SelectableText(account.title),
          ),
          ListTile(
            title: const Text(Strings.username),
            subtitle: SelectableText(account.username),
          ),
          ListTile(
            title: const Text(Strings.password),
            subtitle: SelectableText(account.password),
          ),
          ListTile(
            title: const Text(Strings.package),
            subtitle: SelectableText(account.packageName),
          ),
        ],
      ),
    );
  }
}             
            