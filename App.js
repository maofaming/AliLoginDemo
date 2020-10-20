/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  NativeEventEmitter,
  NativeModules,
  TouchableWithoutFeedback,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

import AliPhoneLogin from './AliPhoneLogin';

const App: () => React$Node = () => {
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <View style={styles.body}>
            <TouchableWithoutFeedback
              onPress={() => {
                AliPhoneLogin.init()
                  .then((res) => {
                    AliPhoneLogin.openLoginAuth({
                      privacy: [
                        {
                          text: '用户协议',
                          url: 'https://www.baidu.com',
                        },
                        {
                          text: '隐私政策',
                          url: 'https://www.baidu.com',
                        },
                      ],
                      openLogin(params) {
                        console.log('openLogin', params);
                      },
                      loginResult(params) {
                        console.log('loginResult', params);
                      },
                    });
                  })
                  .catch((error) => {
                    console.log(error);
                  });
              }}>
              <View style={styles.sectionContainer}>
                <Text style={styles.sectionTitle}>登录</Text>
              </View>
            </TouchableWithoutFeedback>
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
