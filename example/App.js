import React, { Component } from 'react';
import {
  Button,
  FlatList,
  NativeEventEmitter,
  NativeModules,
  StyleSheet,
  Text,
  View
} from 'react-native';
import BoltSDK from 'react-native-card-connect';

const SITE_ID = "isv-uat";
const SITE_URL = `${SITE_ID}.cardconnect.com`;

export default class App extends Component {

  state = {
    devices: {},
    isDiscovering: false,
    testCard: {
      number: '4111111111111111',
      expirationDate: '12/29',
      cvv: '123',
      isTokenizing: false,
      token: null,
      tokenError: null
    },
    device: {
      macAddress: null,
      isConnecting: false,
      isConnected: false,
      token: null,
      isActivating: false,
      isActive: false,
      error: null
    },
    deviceLogs: []
  };

  eventListeners = [];
  eventEmitter = null;

  componentDidMount() {

    BoltSDK.setupConsumerApiEndpoint(SITE_URL);

    this.eventEmitter = new NativeEventEmitter(NativeModules.BoltSDK);

    this.eventListeners.push(this.eventEmitter.addListener('BoltDeviceFound', this.onDeviceFound));

    this.eventListeners.push(this.eventEmitter.addListener('BoltOnTokenGenerated', this.onTokenGenerated));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnTokenGeneratedError', this.onTokenGeneratedError));

    this.eventListeners.push(this.eventEmitter.addListener('BoltOnSwiperConnected', this.onDeviceConnected));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnSwiperDisconnected', this.onDeviceDisconnected));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnSwiperReady', this.onDeviceReady));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnSwipeError', this.logEvent));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnTokenGenerationStart', this.logEvent));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnRemoveCardRequested', this.logEvent));

    this.eventListeners.push(this.eventEmitter.addListener('BoltOnBatteryState', this.logEvent));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnLogUpdate', this.captureLog));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnDeviceConfigurationUpdate', this.captureConfigLog));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnDeviceConfigurationProgressUpdate', this.logEvent));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnDeviceConfigurationUpdateComplete', this.logEvent));

    this.eventListeners.push(this.eventEmitter.addListener('BoltOnTimeout', this.onDeviceTimeout));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnCardRemoved', this.logEvent));
    this.eventListeners.push(this.eventEmitter.addListener('BoltOnDeviceBusy', this.logEvent));
  }

  logEvent = (params) => {

    console.log('event received');
    console.log(params);
  }

  captureLog = ({ log }) => {

    this.setState({
      deviceLogs: [ ...this.state.deviceLogs, log ]
    });
  }

  captureConfigLog= ({ configUpdate }) => {

    this.setState({
      deviceLogs: [ ...this.state.deviceLogs, configUpdate ]
    });
  }

  onDeviceConnected = () => {

    this.setState({
      device: {
        ...this.state.device,
        isConnected: true,
        isConnecting: false
      }
    });
  }

  onDeviceDisconnected = () => {

    this.setState({
      device: {
        ...this.state.device,
        isConnected: false
      }
    });
  }

  onDeviceTimeout = () => {

    this.setState({
      device: {
        ...this.state.device,
        isActive: false
      }
    });
  }

  // the Bolt SDK will return the same device multiplle times, so we'll keep all
  // stored devices unique by storing them keyed on their macAddress
  onDeviceFound = (device) => {

    this.setState({
      devices: {
        ...this.state.devices,
        [device.macAddress]: device
      }
    });
  }

  onDeviceReady = () => {

    this.setState({
      device: {
        ...this.state.device,
        isActivating: false,
        isActive: true
      }
    });
  }

  onTokenGenerated = ({ token, name }) => {

    this.setState({
      device: {
        ...this.state.device,
        token: params.token,
        isActive: false
      }
    });
  }

  onTokenGeneratedError = ({ responseError, responseCode }) => {

    this.setState({
      device: {
        ...this.state.device,
        isActive: false,
        error: `Error ${responseCode}: ${responseError}`
      }
    });
  }

  componentWillUnmount() {

    let listener;
    while(listener = this.eventListeners.pop()) {
      listener.remove();
    }
  }

  tokenizeCard = async () => {

    try {
      this.setState({
        testCard: {
          ...this.state.testCard,
          isTokenizing: true,
          tokenError: null,
          token: null
        }
      });

      const token = await BoltSDK.getCardToken(
        this.state.testCard.number,
        this.state.testCard.expirationDate,
        this.state.testCard.cvv
      );

      this.setState({
        testCard: {
          ...this.state.testCard,
          isTokenizing: false,
          token
        }
      });
    } catch (error) {

      this.setState({
        testCard: {
          ...this.state.testCard,
          isTokenizing: false,
          tokenError: error.toString()
        }
      });
    }
  }

  onPressDiscover = () => {

    this.setState({ isDiscovering: true });
    BoltSDK.discoverDevice();
  }

  onPressActivateDevice = () => {

    this.setState({
      device: {
        ...this.state.device,
        isActivating: true,
        error: null
      }
    });
    BoltSDK.activateDevice();
  }

  connectToDevice = (macAddress) => {

    this.setState({
      isDiscovering: false,
      device: {
        ...this.state.device,
        isConnecting: true
      }
    });
    BoltSDK.connectToDevice(macAddress);
  }

  render() {

    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>☆Bolt SDK Example☆</Text>
        <Text style={styles.testCardHeader}>Test Card:</Text>
        <Text>{this.state.testCard.number}</Text>
        <Text>{this.state.testCard.expirationDate}</Text>
        <Text style={{ marginBottom: 10 }}>{this.state.testCard.cvv}</Text>
        {this.state.testCard.token && (
          <>
            <Text style={styles.testCardHeader}>Token:</Text>
            <Text style={{ marginBottom: 10 }}>{this.state.testCard.token}</Text>
          </>
        )}
        <Button
          title='Tokenize Test Card'
          onPress={this.tokenizeCard}
          disabled={this.state.testCard.isTokenizing}
          style={{ marginBottom: 15 }}
        />
        {!(this.state.device.isConnected || this.state.device.isConnecting) && (
          <>
            {!this.state.isDiscovering &&  (
              <Button
                title='Discover'
                onPress={this.onPressDiscover}
              />
            )}
            {this.state.isDiscovering && (
              <FlatList
                data={Object.values(this.state.devices)}
                renderItem={({ item }) => (

                  <Text style={styles.item} onPress={() => this.connectToDevice(item.macAddress)}>{item.name}</Text>
                )}
                keyExtractor={(item) => item.macAddress}
              />
            )}
          </>
        )}
        {this.state.device.isConnecting && (
          <Text>Connecting to device</Text>
        )}
        {this.state.device.isActivating && (
          <Text>Activating device</Text>
        )}
        {this.state.device.isConnected && !(this.state.device.isActivating || this.state.device.isActive) && (
          <Button
            title='Activate Device'
            onPress={this.onPressActivateDevice}
          />
        )}
        {this.state.device.isActive && (
          <Text>Device ready: swipe or insert card</Text>
        )}
        {this.state.device.error && (
          <Text>{this.state.device.error}</Text>
        )}
        <Text style={styles.welcome}>☆Device Logs☆</Text>
        {this.state.deviceLogs.map((log, index) => (

          <Text key={index}>{log}</Text>
        ))}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'flex-start',
    backgroundColor: '#F5FCFF',
    margin: 20
  },
  testCardHeader: {
    fontWeight: 'bold'
  },
  welcome: {
    fontSize: 20,
    marginTop: 10,
    marginBottom: 10
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  item: {
    backgroundColor: '#f9c2ff',
    padding: 20,
    marginVertical: 8,
    marginHorizontal: 16,
  },
});
