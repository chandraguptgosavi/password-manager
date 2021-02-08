import 'package:flutter/material.dart';
import 'package:password_manager/bloc/bloc.dart';
import 'package:password_manager/ui/screens/details.dart';
import 'package:password_manager/ui/screens/enter_details.dart';
import 'package:password_manager/ui/widgets/drawer.dart';
import 'package:password_manager/values/dimens.dart';
import 'package:password_manager/values/strings.dart';
import 'package:provider/provider.dart';

class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  Bloc _bloc;

  @override
  void initState() {
    super.initState();
    _bloc = context.read<Bloc>()..init();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(Strings.app_name),
      ),
      body: StreamBuilder(
        stream: _bloc.accountsStream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return ListView.builder(
              itemCount: snapshot.data.length,
              itemBuilder: (context, index) {
                var account = snapshot.data[index];
                return Padding(
                  padding: const EdgeInsets.all(Dimens.small_spacing),
                  child: Dismissible(
                    key: UniqueKey(),
                    background: Container(color: Colors.red),
                    onDismissed: (direction) async {
                      await _bloc.deleteAccount(account);
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: const Text(Strings.deleted_successfully),
                        ),
                      );
                    },
                    child: Card(
                      elevation: Dimens.small_spacing,
                      child: ListTile(
                        contentPadding: const EdgeInsets.all(Dimens.small_spacing),
                        onTap: () async {
                          Navigator.of(context).push(
                            MaterialPageRoute(
                              builder: (context) {
                                return DetailsPage(
                                  account: account,
                                );
                              },
                            ),
                          );
                        },
                        title: Text(account.title),
                      ),
                    ),
                  ),
                );
              },
            );
          }
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(
              child: const CircularProgressIndicator(),
            );
          }
          return Container();
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => Navigator.of(context).push(
          MaterialPageRoute(
            builder: (context) {
              return EnterDetailsPage();
            },
          ),
        ),
        child: const Icon(Icons.add),
      ),
      drawer: NavigationDrawer(),
    );
  }

  @override
  void dispose() {
    super.dispose();
    _bloc.dispose();
  }
}
