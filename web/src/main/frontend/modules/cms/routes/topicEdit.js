import React from 'react'
import PropTypes from 'prop-types';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { routerPath } from '../../../constants'
import Edit from '../../../components/topic/topicEdit'


function TopicEdit({
	location,
	dispatch,
	cms,
	topic
}, context) {
	let editProps = {
		catgs: topic.catgs,
		topic: topic.base,
		fileList: topic.fileList,
		dispatch:dispatch,
		goBack: () => {
			context.router.goBack();
		},
		saveTopic: (topic) => {
            let success = () => {
				context.router.push({
					pathname: routerPath.ARTICLE_LIST,
					query: {
						...cms.naq,
						categoryId: topic.article.categoryId,
						block: topic.article.block,
						delFlag: topic.article.delFlag
					}
				});
			};
			const source = location.query.source;
			if(source && source === 'subject') {
				success = () => {
					context.router.goBack();
				};
			}
			const action = topic.id ? 'update' : 'create';
			const type = `topic/effect:${action}:topic`;
			dispatch({
				type,
                payload: {
                    topic,
                    success
                }
			});
		},
        updateState:(obj)=>{
		    dispatch({
                type: 'topic/reducer:update',
                payload:obj
            })
        }
	}
	return (
		<div className='content-inner'>
			<Edit {...editProps}/>
		</div>
	);
}

TopicEdit.contextTypes = {
    router: PropTypes.object.isRequired
}

function mapStateToProps ({ cms, topic }) {
  return { cms, topic }
}

export default connect(mapStateToProps)(TopicEdit)
