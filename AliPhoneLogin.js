import {NativeEventEmitter, NativeModules} from 'react-native';

const {AliPhoneLogin} = NativeModules;
const eventEmitter = new NativeEventEmitter(AliPhoneLogin);

let isInit = false;
let errorInfo = {};

export function getEventName(name = '') {
  return '_' + new Date().getTime() + '_' + name;
}

export default {
  init() {
    const loginKey =
      'lYnLItpjhmAWlraZ3i0riNIw3w2gY7EjqocbTnLtR0zZUb3qDgy+5ba7dNIhetgjYE7EDIxs0905dEiSEwuZKQcNHC7sOGEdPe3wnVVTGqOsmD9k2XfnngPyDfSN7TrzLL0NvJ4KUR2ZsZ7iDfn5ME3099teIY74L4x61Trm2wtUx/12nTsMSGs5cVGv09KfuJjo9Bmff9aEWZ7sStAb9Lg6482b9jWq1mDmN1piq556D4HFXtuadvFIaRxmMrUemb9Rwz2G9p6dIj8a/NM35lTB5XwXJudi93EhWDrwt46HQZRvbAXwKw==';
    return new Promise((resolve, reject) => {
      if (!isInit && typeof loginKey === 'string' && loginKey.length > 0) {
        AliPhoneLogin.init(loginKey)
          .then((res) => {
            console.log('init result', res);
            // if (res.code === '600001' || res.code === '600024') {
            isInit = true;
            resolve(res);
            // } else {
            //   isInit = false;
            //   reject(res);
            // }
          })
          .catch((error) => {
            errorInfo = error;
            reject(error);
          });
      } else if (isInit) {
        resolve(errorInfo);
      } else {
        reject(new Error('appid null'));
      }
    });
  },
  baseModule(actionName, params, ...args) {
    if (isInit) {
      // console.log('baseModule', actionName, params, ...args);
      let eventName = getEventName(actionName);
      let emiter = eventEmitter.addListener(eventName, (data) => {
        // console.log(eventName, data);
        if (params[data.type]) {
          params[data.type](data);
        }
      });
      AliPhoneLogin[actionName](eventName, ...args);
      return emiter;
    }
  },
  openLoginAuth(params = {}) {
    return this.baseModule(
      'getLoginToken',
      params,
      10000, // 10s超时
      params.privacy || [],
    );
  },
  getErrorInfo() {
    return errorInfo;
  },
  getInit() {
    return isInit;
  },
};
