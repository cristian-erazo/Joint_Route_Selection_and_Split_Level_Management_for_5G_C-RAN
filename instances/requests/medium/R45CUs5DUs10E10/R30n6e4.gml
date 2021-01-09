graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 2
    prb 5
    x 62
    y 106
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 2
    prb 5
    x 82
    y 91
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 8
    prb 6
    x 32
    y 22
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 10
    prb 6
    x 53
    y 89
  ]
  edge [
    source 0
    target 3
    bandwith 768
    delay 252
  ]
  edge [
    source 1
    target 5
    bandwith 919
    delay 124
  ]
  edge [
    source 1
    target 2
    bandwith 199
    delay 318
  ]
  edge [
    source 1
    target 4
    bandwith 525
    delay 124
  ]
]
