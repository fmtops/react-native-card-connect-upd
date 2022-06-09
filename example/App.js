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

const SITE_ID = "fts-uat";
const SITE_URL = `https://${SITE_ID}.cardconnect.com/cardconnect/rest`;

export default class App extends Component {

  state = {
    devices: {},
    card: {
      number: '4111111111111111',
      expiryDate: '12/22',
      cvv: '123'
    }
  };

  componentDidMount() {

    BoltSDK.setupConsumerApiEndpoint(SITE_URL);

    const eventEmitter = new NativeEventEmitter(NativeModules.BoltSDK);
    this.eventListener = eventEmitter.addListener('DeviceFound', this.onDeviceFound);
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

  componentWillUnmount() {
    this.eventListener.remove();
  }

  async tokenizeCard() {

    try {
      const token = await BoltSDK.getCardToken(
        this.state.card.number,
        this.state.card.expiryDate,
        this.state.card.cvv
      );

      console.log(token);
    } catch (error) {

      console.log(error.toString());
    }
  }

  render() {

    const onPressDiscover = () => {

      BoltSDK.discoverDevice();
    };

    const onPressActivateDevice = () => {

      BoltSDK.activateDevice();
    };

    const connectToDevice = (macAddress) => {

      BoltSDK.connectToDevice(macAddress);
    };

    const onTokenizeCard = () => {

      this.tokenizeCard();
    };

    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>☆Bolt SDK Example☆</Text>
        <Button 
          title='Tokenize Card'
          onPress={onTokenizeCard}
        />
        <Button 
          title='Discover'
          onPress={onPressDiscover}
        />
        <Button 
          title='Activate Device'
          onPress={onPressActivateDevice}
        />
        {Object.keys(this.state.devices).length > 0 && (
          <FlatList
            data={Object.values(this.state.devices)}
            renderItem={({ item }) => (
            
              <Text style={styles.item} onPress={() => connectToDevice(item.macAddress)}>{item.name}</Text>
            )}
            keyExtractor={item => item.macAddress}
          />
          /*<Button
            key={macAddress}
            title={`Connect ${this.state.devices[macAddress].name}`}
            onPress={() => connectToDevice(macAddress)}
          />*/
        )}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  // container: {
  //   flex: 1,
  //   marginTop: StatusBar.currentHeight || 0,
  // },
  item: {
    backgroundColor: '#f9c2ff',
    padding: 20,
    marginVertical: 8,
    marginHorizontal: 16,
  },
});
