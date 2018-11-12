import React, {Component} from 'react';
import 'bulma';
import axios from 'axios';
import {Section} from 'bloomer';
import Top from './components/Top';
import Middle from './components/Middle';
import Bottom from './components/Bottom';

class Home extends Component {
  constructor(props) {
    super(props);
    this.state = {
      listings: [],
      currentFrameIndex: 0,
      showMy: false,
      currentFrame: []
    };
    this.getListings = this.getListings.bind(this);
    this.setFrame = this.setFrame.bind(this);
    this.nextFrame = this.nextFrame.bind(this);
    this.prevFrame = this.prevFrame.bind(this);
    this.showMy = this.showMy.bind(this);
    this.getListings();
    this.setFrame();
  }
  showMy() {
    this.getListings();
    this.setState(currentState => ({
      showMy: !currentState.showMy
    }));
  }
  setFrame() {
    let Frame = this.state.listings.slice(this.state.currentFrameIndex, this.state.currentFrameIndex + 6);
    this.setState({currentFrame: Frame});
  }
  nextFrame() {
    this.getListings();
    if ((this.state.currentFrameIndex+6) >= this.state.listings.length)
      return;
    this.setState(currentState => ({
      currentFrameIndex: currentState.currentFrameIndex + 6
    }));
  }
  prevFrame() {
    this.getListings();
    if ((this.state.currentFrameIndex-6) < 0)
      return;
    this.setState(currentState => ({
      currentFrameIndex: currentState.currentFrameIndex - 6
    }));
  }
  getListings() {
    if (!this.state.showMy) {
      axios.get("Listing").then(res => {
        this.setState({
          listings: JSON.parse(res.data.listings)
        });
        this.setFrame();
        Middle.forceUpdate();
      });
    } else {
      const fd = new FormData();
      fd.append("showMy", "true");
      axios.get("Listing", fd).then(res => {
        this.setState({
          listings: JSON.parse(res.data.listings)
        });
        this.setFrame();
      });
    }
  }
  render() {
    return (<div>
      <Top logout={this.props.logout} showMy={this.showMy}/>
      <Section>
        <Middle listings={this.state.currentFrame} trigger={this.getListings} uid={this.props.uid}/>
      </Section>
      <Section>
        <Bottom next={this.nextFrame} prev={this.prevFrame}/>
      </Section>
    </div>);
  }
}

export default Home;
