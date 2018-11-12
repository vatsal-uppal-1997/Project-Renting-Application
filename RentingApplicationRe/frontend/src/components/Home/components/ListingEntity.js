import React, {Component} from 'react';
import 'bulma';
import {
  Card,
  CardHeader,
  CardHeaderTitle,
  CardFooter,
  CardFooterItem,
  CardImage,
  Image,
  CardContent,
  Content
} from 'bloomer';

class ListingEntity extends Component {
  constructor(props) {
    super(props);
    this.setText = this.setText.bind(this);
    this.edit = this.edit.bind(this);
    this.delete = this.delete.bind(this);
    this.contact = this.contact.bind(this);
    this.interested = this.interested.bind(this);
  }
  edit() {
    let defaultValues = {
      'id' : this.props.lid,
      'title' : this.props.title,
      'locality' : this.props.locality,
      'description' : this.props.description
    }
    console.log("defaultValues "+defaultValues);
    this.props.enableEdit(defaultValues);
  }
  delete() {

  }
  contact() {

  }
  interested() {

  }
  setText() {
    if (this.props.uid === this.props.luid) {
      return (<CardFooter>
        <CardFooterItem onClick={this.edit}>Edit</CardFooterItem>
        <CardFooterItem onClick={this.delete}>Delete</CardFooterItem>
      </CardFooter>);
    } else {
      return (<CardFooter>
        <CardFooterItem onClick={this.contact}>Contact</CardFooterItem>
        <CardFooterItem onClick={this.interested}>Interested</CardFooterItem>
      </CardFooter>);
    }
  }
  render() {
    return (<Card>
      <CardHeader>
        <CardHeaderTitle>
          {this.props.title}
        </CardHeaderTitle>
      </CardHeader>
      <CardImage>
        <Image isRatio='square' src={this.props.image}/>
      </CardImage>
      <CardContent>
        <Content>
          {this.props.description}
        </Content>
      </CardContent>
      {this.setText()}
    </Card>);
  }
}

export default ListingEntity;
