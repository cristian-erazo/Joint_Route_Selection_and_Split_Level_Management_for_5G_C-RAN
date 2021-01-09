graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 3
    prb 5
    x 101
    y 24
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 3
    prb 3
    x 90
    y 74
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 6
    prb 3
    x 43
    y 93
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 5
    prb 2
    x 40
    y 70
  ]
  edge [
    source 0
    target 5
    bandwith 836
    delay 183
  ]
  edge [
    source 0
    target 4
    bandwith 882
    delay 455
  ]
  edge [
    source 1
    target 3
    bandwith 837
    delay 200
  ]
  edge [
    source 1
    target 2
    bandwith 859
    delay 172
  ]
]
