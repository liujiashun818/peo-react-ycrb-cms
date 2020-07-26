import React from 'react'
import { Row, Col, Collapse} from 'antd'
import Submiter from '../form/submit'

function DetailEdit ({ children }) {
  return (
    <Row gutter={24}>
    {
        React.Children.map(children, function (child) {
        if(child.props.type == 'sidebar'){ /* 侧边栏 */
          return (
            <Col span={6}>
                {child}
            </Col>
         );
        }else{  /* 主栏 */
          return <Col span={18}>{child}</Col>;
        }
        })
    }
    </Row>
  )
}

export default DetailEdit
