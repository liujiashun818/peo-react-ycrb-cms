/* @flow */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';

import { getFirstIcon } from './util/toolbar';
import { Dropdown, DropdownOption } from './dropdown/dropdown';
import Option from './option/option';
import './list-component.css';

export default class LayoutComponent extends Component {

  option = ['unordered', 'ordered', 'indent', 'outdent'];

  toggleBlockType = (blockType) => {
    const { onChange } = this.props;
    onChange(blockType);
  };

  indent = () => {
    const { onChange } = this.props;
    // adjustDepth: Function = (adjustment): void => {
    //     const { onChange, editorState } = this.props;
    //     const newState = changeDepth(
    //       editorState,
    //       adjustment,
    //       4,
    //     );
    //     if (newState) {
    //       onChange(newState);
    //     }
    //   };
    onChange('indent');
  };

  outdent = () => {
    const { onChange } = this.props;
    onChange('outdent');
  };

  // todo: evaluate refactoring this code to put a loop there and in other places also in code
  // hint: it will require moving click handlers
  renderInFlatList(){
    const {
      config,
      currentState: { listType },
      translations,
      indentDisabled,
      outdentDisabled
    } = this.props;
    const { options, unordered, ordered, indent, outdent, className } = config;
    return (
      <div className={classNames('rdw-list-wrapper', className)} aria-label="rdw-list-control">
        {options.indexOf('unordered') >= 0 && <Option
          value="unordered"
          onClick={this.toggleBlockType}
          className={classNames(unordered.className)}
          active={listType === 'unordered'}
          title={unordered.title || translations['components.controls.list.unordered']}
        >
          <img
            src={unordered.icon}
            alt=""
          />
        </Option>}
        {options.indexOf('ordered') >= 0 && <Option
          value="ordered"
          onClick={this.toggleBlockType}
          className={classNames(ordered.className)}
          active={listType === 'ordered'}
          title={ordered.title || translations['components.controls.list.ordered']}
        >
          <img
            src={ordered.icon}
            alt=""
          />
        </Option>}
        {options.indexOf('indent') >= 0 && <Option
          onClick={this.indent}
          disabled={indentDisabled}
          className={classNames(indent.className)}
          title={indent.title || translations['components.controls.list.indent']}
        >
          <img
            src={indent.icon}
            alt=""
          />
        </Option>}
        {options.indexOf('outdent') >= 0 && <Option
          onClick={this.outdent}
          disabled={outdentDisabled}
          className={classNames(outdent.className)}
          title={outdent.title || translations['components.controls.list.outdent']}
        >
          <img
            src={outdent.icon}
            alt=""
          />
        </Option>}
      </div>
    );
  }

  renderInDropDown(){
    const {
      config,
      expanded,
      doCollapse,
      doExpand,
      onExpandEvent,
      onChange,
      currentState: { listType },
      translations,
    } = this.props;
    const { options, className, dropdownClassName, title } = config;
    return (
      <Dropdown
        className={classNames('rdw-list-dropdown', className)}
        optionWrapperClassName={classNames(dropdownClassName)}
        onChange={onChange}
        expanded={expanded}
        doExpand={doExpand}
        doCollapse={doCollapse}
        onExpandEvent={onExpandEvent}
        aria-label="rdw-list-control"
        title={title || translations['components.controls.list.list']}
      >
        <img
          src={getFirstIcon(config)}
          alt=""
        />
        { this.options
          .filter(option => options.indexOf(option) >= 0)
          .map((option, index) => (<DropdownOption
            key={index}
            value={option}
            disabled={this.props[`${option}Disabled`]}
            className={classNames('rdw-list-dropdownOption', config[option].className)}
            active={listType === option}
            title={config[option].title || translations[`components.controls.list.${option}`]}
          >
            <img
              src={config[option].icon}
              alt=""
            />
          </DropdownOption>))
        }
      </Dropdown>
    );
  }

  render() {
    const { config: { inDropdown } } = this.props;
    if (inDropdown) {
      return this.renderInDropDown();
    }
    return this.renderInFlatList();
  }
}